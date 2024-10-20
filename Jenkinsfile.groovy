node {
    properties([
        disableConcurrentBuilds()
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
    customImage.inside('--net="jenkins_default"') { // required for accessing the Gitea server
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
                    stage("Build project [${c}]") {
                        sh "./tools/build-cmake-target.sh ${c}-release cplusplus_training_project"
                        archiveArtifacts(
                            artifacts: "build/${c}-release/bin/cplusplus_training_project",
                            onlyIfSuccessful: true
                        )
                        recordIssues(
                            sourceCodeRetention: 'LAST_BUILD',
                            tools: [
                                "${c}"() // use dynamic method invocation
                            ]
                        )
                    }
                    stage("Build tests [${c}]") {
                        sh "./tools/build-cmake-target.sh ${c}-coverage calculate_test"
                        archiveArtifacts(
                            artifacts: "build/${c}-coverage/bin/calculate_test",
                            onlyIfSuccessful: true
                        )
                    }
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
                        sh "./tools/run-test.sh build/${c}-coverage/bin/calculate_test build/${c}"
                        junit(
                            testResults: "build/${c}/test-report.xml"
                        )
                        def coverage_name = c == 'gcc' ? 'GCC' : 'Clang'
                        recordCoverage(
                            name: "${coverage_name} Coverage",
                            id: "${c}-coverage",
                            tools: [
                                [parser: 'JUNIT', pattern: "build/${c}/test-report.xml"],
                                [parser: 'COBERTURA', pattern: "build/${c}/test-coverage.xml"]
                            ]
                        )
                        publishHTML([
                            allowMissing: false,
                            alwaysLinkToLastBuild: false,
                            keepAll: false,
                            reportDir: "build/${c}/html",
                            reportFiles: 'index.html',
                            reportName: "${coverage_name} Coverage HTML",
                            reportTitles: '',
                            useWrapperFileDirectly: true
                        ])
                    }
                }
            }
            parallel tests
        }
    }
}
