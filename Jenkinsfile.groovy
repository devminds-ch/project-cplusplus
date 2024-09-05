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
        stage('Build project') {
            sh 'cmake --preset default'
            sh 'cmake --build --preset default --target cplusplus_training_project'
            archiveArtifacts(
                artifacts: 'build/default/bin/cplusplus_training_project',
                onlyIfSuccessful: true
            )
        }
        stage('Build tests') {
            sh 'cmake --preset coverage'
            sh 'cmake --build --preset coverage --target calculate_test'
            archiveArtifacts(
                artifacts: 'build/coverage/bin/calculate_test',
                onlyIfSuccessful: true
            )
        }
        stage('Run tests') {
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
