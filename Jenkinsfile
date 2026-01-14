pipeline {
    agent any

    stages {
        stage('Test') {
            steps {
                bat 'gradlew.bat test --no-daemon'
            }
            post {
                always {
                    junit 'build/test-results/test/TEST-*.xml'
                    // Si vous n’avez pas de Cucumber, commentez ou supprimez la ligne suivante :
                    cucumber reports: [jsonReportDirectory: 'build/cucumber-reports']
                }
            }
        }

        stage('Code Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    bat 'gradlew.bat sonarqube --no-daemon'
                }
            }
        }

        stage('Code Quality') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Build') {
            steps {
                bat 'gradlew.bat jar javadoc --no-daemon'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
                    archiveArtifacts artifacts: 'build/docs/javadoc/**', allowEmptyArchive: true
                }
            }
        }

        stage('Deploy') {
            steps {
                bat 'gradlew.bat publish --no-daemon'
            }
        }
    }

    post {
        success {
            emailext (
                subject: "✅ Build réussi : ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                body: "Le pipeline s'est terminé avec succès.\nVoir ici : ${env.BUILD_URL}",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }
        failure {
            emailext (
                subject: "❌ Échec du build : ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                body: "Le pipeline a échoué.\nVoir ici : ${env.BUILD_URL}",
                recipientProviders: [[$class: 'DevelopersRecipientProvider']]
            )
        }
    }
}