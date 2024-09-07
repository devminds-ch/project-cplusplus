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
                    /*
                    stage("Build project [${c}]") {
                        sh "cmake --preset ${c}-release"
                        sh "cmake --build --preset ${c}-release --target cplusplus_training_project"
                        archiveArtifacts(
                            artifacts: "build/${c}-release/bin/cplusplus_training_project",
                            onlyIfSuccessful: true
                        )
                    }
                    stage("Build tests [${c}]") {
                        sh "cmake --preset ${c}-coverage"
                        sh "cmake --build --preset ${c}-coverage --target calculate_test"
                        archiveArtifacts(
                            artifacts: "build/${c}-coverage/bin/calculate_test",
                            onlyIfSuccessful: true
                        )
                    }
                    */
                }
            }
            parallel builds
        }
        stage('Parallel test execution')
        {
            def tests = [:]
            compilers.each { c ->
                tests[c] = {
                    stage("Run tests [${c}]") {
                        sh "./build/${c}-coverage/bin/calculate_test --gtest_output='xml:report-${c}.xml'"
                        // Create Cobertura coverage report
                        def gcov_executable = c == 'gcc' ? 'gcov' : 'llvm-cov gcov'
                        sh "gcovr --gcov-executable '${gcov_executable}' -f src . --root ./ --exclude-unreachable-branches --xml-pretty --print-summary -o 'coverage-${c}.xml'"

                        if (c == "gcc") {
                            // Create HTML coverage report
                            sh 'mkdir -p gcov'
                            sh 'gcovr -f src . --root ./ --exclude-unreachable-branches --html --html-details -o "gcov/index.html"'
                            publishHTML([
                                allowMissing: false,
                                alwaysLinkToLastBuild: false,
                                keepAll: false,
                                reportDir: 'gcov',
                                reportFiles: 'index.html',
                                reportName: 'Coverage',
                                reportTitles: '',
                                useWrapperFileDirectly: true
                            ])
                            junit(
                                testResults: "report-${c}.xml"
                            )
                        }
                        def coverage_name = c == 'gcc' ? 'GCC' : 'Clang'
                        recordCoverage(
                            name: "${coverage_name} Coverage",
                            tools: [
                                [parser: 'JUNIT', pattern: "report-${c}.xml"],
                                [parser: 'COBERTURA', pattern: "coverage-${c}.xml"]
                            ]
                        )
                    }
                }
            }
            parallel tests
        }
    }
}
