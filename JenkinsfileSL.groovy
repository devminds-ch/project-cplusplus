@Library('my-shared-library@main') _

log.info 'Starting...'

node {
    properties([
        disableConcurrentBuilds(),
        parameters([
            booleanParam(
                name: 'RUN_TESTS',
                defaultValue: true,
                description: 'Flag indicating if tests should be executed'
            )
        ])
    ])
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
        docker.withRegistry('http://gitea.lan:3000', 'gitea') {
            customImage = docker.build(
                "root/jenkins-cplusplus:latest",
                "-f .devcontainer/Dockerfile ./")
            customImage.push() // push custom image to the own registry
        }
    }
    customImage.inside {
        stage('Cleanup') {
            sh 'rm -rf build'
        }
        stage('Build documentation') {
            sh './tools/build-docs.sh'
            archiveArtifacts(
                artifacts: 'build/html/**',
                onlyIfSuccessful: true
            )
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: false,
                keepAll: false,
                reportDir: 'build/html/',
                reportFiles: 'index.html',
                reportName: 'Documentation',
                reportTitles: '',
                useWrapperFileDirectly: true
            ])
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
            when(params.RUN_TESTS) {
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
}
