pipeline {
    agent any
    options {
        disableConcurrentBuilds()
    }
    triggers {
        pollSCM "* * * * *"
    }
    environment {
        pom = readMavenPom()
        version = pom.getVersion()
    }
    parameters {
        booleanParam(name: "RELEASE", description: "Build a release from current commit.", defaultValue: false)
        booleanParam(name: "FORCE_PUBLISH", description: "Allow artifact publish on a branch other than develop or master", defaultValue: false)
    }
    stages {
        stage ('build') {
            steps {
                sh 'mvn -e -U -B clean package -DskipTests=true'
            }
        }
        stage ('unit test') {
            steps {
                sh 'mvn -e -U -B test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }
        stage ('publish snapshot') {
            when {
                allOf {
                    not {
                        expression { params.RELEASE }
                    }
                    anyOf {
                        branch 'master'
                        branch 'develop'
                        expression { params.FORCE_PUBLISH }
                    }
                }
            }
            steps {
                sh 'mvn -e -U -B build-helper:parse-version versions:set -DnewVersion=\\${parsedVersion.majorVersion}.\\${parsedVersion.minorVersion}.\\${parsedVersion.incrementalVersion}-SNAPSHOT versions:commit clean package deploy -DskipTests'
            }
        }
        stage ('publish release') {
            when {
                expression { params.RELEASE }
            }
            steps {
                script {
                    if (env.BRANCH_NAME == 'master') {
                        sh 'mvn -e -U -B build-helper:parse-version -Dtag=\\${parsedVersion.majorVersion}.\\${parsedVersion.minorVersion}.\\${parsedVersion.incrementalVersion} release:prepare -DreleaseVersion=\\${parsedVersion.majorVersion}.\\${parsedVersion.minorVersion}.\\${parsedVersion.incrementalVersion} -DdevelopmentVersion=\\${parsedVersion.majorVersion}.\\${parsedVersion.minorVersion}.\\${parsedVersion.nextIncrementalVersion}-SNAPSHOT release:perform'
                    } else {
                        error("RELEASE is only permitted on master branch!")   
                    }
                }
            }
        }
        stage ('remote test') {
            when {
                branch 'develop'   
            }
            steps {
               echo "RUNNING TESTS!!!"    
            }
        }
    }
    post {
        failure {
            echo "FAILED!!!"
        }
    }
}
