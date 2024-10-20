pipeline {
    agent {
        /*docker {
            image 'mcr.microsoft.com/devcontainers/python:1-3.12-bookworm'
        }*/
        dockerfile {
            filename '.devcontainer/Dockerfile'
            dir '.'
            args  '--net="jenkins_default"' // required for accessing the Gitea server
        }
    }
    options {
        disableConcurrentBuilds()
        //skipDefaultCheckout() // default checkout is required for .devcontainer/Dockerfile
        //newContainerPerStage()
    }
    parameters {
        booleanParam(
            name: 'RUN_TESTS',
            defaultValue: true,
            description: 'Flag indicating if tests should be executed'
        )
    }
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
        stage('Build documentation') {
            steps {
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
                            sh "./tools/build-cmake-target.sh ${COMPILER}-release cplusplus_training_project"
                            archiveArtifacts(
                                artifacts: "build/${COMPILER}-release/bin/cplusplus_training_project",
                                onlyIfSuccessful: true
                            )
                            recordIssues(
                                sourceCodeRetention: 'LAST_BUILD',
                                tools: [
                                    gcc(
                                        id: "gcc-${COMPILER}",
                                        name: "GCC [${COMPILER}]"
                                    ),
                                    clang(
                                        id: "clang-${COMPILER}",
                                        name: "Clang [${COMPILER}]"
                                    )
                                ]
                            )
                        }
                    }
                    stage('Build tests') {
                        steps {
                            sh "./tools/build-cmake-target.sh ${COMPILER}-coverage calculate_test"
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
            when {
                expression {
                    params.RUN_TESTS == true
                }
            }

            parallel {
                stage('Run tests [gcc]') {
                    steps {
                        sh './tools/run-test.sh build/gcc-coverage/bin/calculate_test build/gcc'
                        junit(
                            testResults: 'build/gcc/test-report.xml'
                        )
                        recordCoverage(
                            name: 'GCC Coverage',
                            id: 'gcc-coverage',
                            tools: [
                                [parser: 'JUNIT', pattern: 'build/gcc/test-report.xml'],
                                [parser: 'COBERTURA', pattern: 'build/gcc/test-coverage.xml']
                            ]
                        )
                        publishHTML([
                            allowMissing: false,
                            alwaysLinkToLastBuild: false,
                            keepAll: false,
                            reportDir: 'build/gcc/html',
                            reportFiles: 'index.html',
                            reportName: 'GCC Coverage HTML',
                            reportTitles: '',
                            useWrapperFileDirectly: true
                        ])
                    }
                }
                stage('Run tests [clang]') {
                    steps {
                        sh './tools/run-test.sh build/clang-coverage/bin/calculate_test build/clang'
                        junit(
                            testResults: 'build/clang/test-report.xml'
                        )
                        recordCoverage(
                            name: 'Clang Coverage',
                            id: 'clang-coverage',
                            tools: [
                                [parser: 'JUNIT', pattern: 'build/clang/test-report.xml'],
                                [parser: 'COBERTURA', pattern: 'build/clang/test-coverage.xml']
                            ]
                        )
                        publishHTML([
                            allowMissing: false,
                            alwaysLinkToLastBuild: false,
                            keepAll: false,
                            reportDir: 'build/clang/html',
                            reportFiles: 'index.html',
                            reportName: 'Clang Coverage HTML',
                            reportTitles: '',
                            useWrapperFileDirectly: true
                        ])
                    }
                }
            }
        }
    }
}
