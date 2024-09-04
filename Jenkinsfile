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
    //    skipDefaultCheckout()
    //}
    stages {
        stage('Cleanup') {
            steps {
                sh 'rm -rf build'
            }
        }
        stage('Checkout') {
            steps {
                sh 'git submodule update --init --recursive'
                //checkout(
                //    scmGit(
                //        branches: [[name: env.BRANCH_NAME]],
                //        extensions: [submodule(recursiveSubmodules: true, reference: '')],
                //        userRemoteConfigs: [[url: FIXME !!!]]
                //    )
                //)
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
        stage('Build project') {
            steps {
                sh 'cmake --preset default'
                sh 'cmake --build --preset default --target cplusplus_training_project'
                archiveArtifacts(
                    artifacts: 'build/default/bin/cplusplus_training_project',
                    onlyIfSuccessful: true
                )
            }
        }
        stage('Build tests') {
            steps {
                sh 'cmake --preset coverage'
                sh 'cmake --build --preset coverage --target calculate_test'
                archiveArtifacts(
                    artifacts: 'build/coverage/bin/calculate_test',
                    onlyIfSuccessful: true
                )
            }
        }
        stage('Run tests') {
            steps {
                sh './build/coverage/bin/calculate_test --gtest_output="xml:report.xml"'
                // Create Cobertura coverage report
                sh 'gcovr -f src . --root ./ --exclude-unreachable-branches --xml-pretty --print-summary -o "coverage.xml"'
                // Create HTML coverage report
                sh 'mkdir -p gcov'
                sh 'gcovr -f src . --root ./ --exclude-unreachable-branches --html --html-details -o "gcov/index.html"'
                junit(
                    testResults: 'report.xml'
                )
                recordCoverage(
                    tools: [
                        [parser: 'JUNIT', pattern: 'report.xml'],
                        [parser: 'COBERTURA', pattern: 'coverage.xml']
                    ]
                )
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
            }
        }
    }
}