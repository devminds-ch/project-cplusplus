@Library('my-shared-library@main') _

log.info 'Starting...'

node {
    stage('Checkout SCM') {
        checkout(
            scmGit(
                branches: scm.branches,
                extensions: [submodule(recursiveSubmodules: true, reference: '')],
                userRemoteConfigs: scm.userRemoteConfigs
            )
        )
    }
    stage('Agent Setup') {
        // https://www.jenkins.io/doc/book/pipeline/docker/
        customImage = docker.build("jenkins-cplusplus:latest",
                                    "-f .devcontainer/Dockerfile ./")
    }
    customImage.inside {
        stage('Cleanup') {
            sh 'rm -rf build'
        }
        stage('Static code analysis') {
            warnError('clang-format issues found') {
                sh './tools/clang-format.sh'
            }
            sh 'cppcheck src/ --xml --xml-version=2 2> cppcheck.xml'
            recordIssues(
                sourceCodeRetention: 'LAST_BUILD',
                tools: [
                    taskScanner(
                        highTags: 'FIXME',
                        includePattern: 'src/**/*.hpp,src/**/*.cpp',
                        lowTags: 'HACK',
                        normalTags: 'TODO'
                    ),
                    gcc(),
                    cppCheck(pattern: 'cppcheck.xml')
                ]
            )
        }
        def compilers = ["gcc", "clang"]
        stage('Parallel build') {
            def builds = [:]
            compilers.each { c ->
                builds[c] = {
                    stageCMakeBuild(
                        preset: "${c}-release",
                        target: 'cplusplus_training_project',
                        artifacts: "build/${c}-release/bin/cplusplus_training_project"
                    )
                    stageCMakeBuild(
                        preset: "${c}-coverage",
                        target: 'calculate_test',
                        artifacts: "build/${c}-coverage/bin/calculate_test"
                    )
                }
            }
            parallel builds
        }
        stage('Parallel test execution')
        {
            def tests = [:]
            compilers.each { c ->
                tests[c] = {
                    def gcov_executable = c == 'gcc' ? 'gcov' : 'llvm-cov gcov'
                    stageRunTests(
                        run: "./build/${c}-coverage/bin/calculate_test",
                        name: "Calculate test [${c}]",
                        gcov_executable: gcov_executable
                    )
                }
            }
            parallel tests
        }
    }
}
