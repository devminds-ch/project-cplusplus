pipeline {
    agent {
        /*docker {
            image 'mcr.microsoft.com/devcontainers/python:1-3.12-bookworm'
        }*/
        dockerfile {
            filename '.devcontainer/Dockerfile'
            dir '.'
        }
    }
    //options {
    //    skipDefaultCheckout() // default checkout is required for .devcontainer/Dockerfile
    //}
    stages {
        stage('Cleanup') {
            steps {
                sh 'rm -rf build'
            }
        }
        stage('Checkout') {
            steps {
                //sh 'git submodule update --init --recursive'
                checkout(
                    scmGit(
                        branches: scm.branches,
                        extensions: [submodule(recursiveSubmodules: true, reference: '')],
                        userRemoteConfigs: scm.userRemoteConfigs
                    )
                )
            }
        }
        stage('Static code analysis') {
            steps {
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
        }
        stage('Parallel build') {
            matrix {
                axes {
                    axis {
                        name 'COMPILER'
                        values 'gcc', 'clang'
                    }
                }
                stages {
                    stage('Build project') {
                        steps {
                            sh "cmake --preset ${COMPILER}-release"
                            sh "cmake --build --preset ${COMPILER}-release --target cplusplus_training_project"
                            archiveArtifacts(
                                artifacts: "build/${COMPILER}-release/bin/cplusplus_training_project",
                                onlyIfSuccessful: true
                            )
                        }
                    }
                    stage('Build tests') {
                        steps {
                            sh "cmake --preset ${COMPILER}-coverage"
                            sh "cmake --build --preset ${COMPILER}-coverage --target calculate_test"
                            archiveArtifacts(
                                artifacts: "build/${COMPILER}-coverage/bin/calculate_test",
                                onlyIfSuccessful: true
                            )
                        }
                    }
                }
            }
        }
        stage('Parallel test execution') {
            parallel {
                stage('Run tests [gcc]') {
                    steps {
                        sh './build/gcc-coverage/bin/calculate_test --gtest_output="xml:report-gcc.xml"'
                        // Create Cobertura coverage report
                        sh 'gcovr -f src . --root ./ --exclude-unreachable-branches --xml-pretty --print-summary -o "coverage-gcc.xml"'
                        // Create HTML coverage report
                        sh 'mkdir -p gcov'
                        sh 'gcovr -f src . --root ./ --exclude-unreachable-branches --html --html-details -o "gcov/index.html"'
                        publishHTML([
                            allowMissing: false,
                            alwaysLinkToLastBuild: false,
                            keepAll: false,
                            reportDir: 'gcov',
                            reportFiles: 'index.html',
                            reportName: 'Coverage HTML',
                            reportTitles: '',
                            useWrapperFileDirectly: true
                        ])
                        junit(
                            testResults: 'report-gcc.xml'
                        )
                        recordCoverage(
                            name: 'GCC Coverage',
                            id: 'gcc-coverage',
                            tools: [
                                [parser: 'JUNIT', pattern: 'report-gcc.xml'],
                                [parser: 'COBERTURA', pattern: 'coverage-gcc.xml']
                            ]
                        )
                    }
                }
                stage('Run tests [clang]') {
                    steps {
                        sh './build/clang-coverage/bin/calculate_test --gtest_output="xml:report-clang.xml"'
                        // Create Cobertura coverage report
                        sh 'gcovr --gcov-executable "llvm-cov gcov" -f src . --root ./ --exclude-unreachable-branches --xml-pretty --print-summary -o "coverage-clang.xml"'
                        recordCoverage(
                            name: 'Clang Coverage',
                            id: 'clang-coverage',
                            tools: [
                                [parser: 'JUNIT', pattern: 'report-clang.xml'],
                                [parser: 'COBERTURA', pattern: 'coverage-clang.xml']
                            ]
                        )
                    }
                }
            }
        }
    }
}
