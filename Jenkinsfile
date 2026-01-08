pipeline {
    agent any

    stages {
        // ========== PHASE 1 : TEST ==========
        stage('Test') {
            steps {
                echo '🧪 Lancement des tests...'

                script {
                    // Tests unitaires
                    bat 'gradlew.bat test'

                    // Tests Cucumber (si vous avez des tests Cucumber)
                    bat 'gradlew.bat cucumber'
                }
            }
            post {
                always {
                    // Archiver les résultats des tests unitaires
                    junit '**/build/test-results/test/*.xml'

                    // Rapport Cucumber
                    cucumber buildStatus: 'UNSTABLE',
                             fileIncludePattern: '**/cucumber-report.json',
                             trendsLimit: 10
                }
            }
        }

        // ========== PHASE 2 : CODE ANALYSIS ==========
        stage('Code Analysis') {
            steps {
                echo '🔍 Analyse de la qualité du code avec SonarQube...'

                withSonarQubeEnv('SonarQube') {
                    bat 'gradlew.bat sonarqube'
                }
            }
        }

        // ========== PHASE 3 : QUALITY GATE ==========
        stage('Quality Gate') {
            steps {
                echo '🎯 Vérification du Quality Gate...'

                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // ========== PHASE 4 : BUILD ==========
        stage('Build') {
            steps {
                echo '🔨 Construction de l application...'

                // Générer le JAR
                bat 'gradlew.bat clean build -x test'

                // Générer la Javadoc
                bat 'gradlew.bat javadoc'
            }
            post {
                success {
                    echo '✅ Archivage des artifacts...'

                    // Archiver le JAR
                    archiveArtifacts artifacts: '**/build/libs/*.jar',
                                     fingerprint: true,
                                     allowEmptyArchive: true

                    // Archiver la documentation
                    archiveArtifacts artifacts: '**/build/docs/javadoc/**',
                                     fingerprint: true,
                                     allowEmptyArchive: true
                }
            }
        }

        // ========== PHASE 5 : DEPLOY ==========
        stage('Deploy') {
            when {
                branch 'main'  // Déployer uniquement depuis la branche main
            }
            steps {
                echo '🚀 Déploiement sur MyMavenRepo...'

                bat 'gradlew.bat publish'
            }
        }
    }

    // ========== PHASE 6 : NOTIFICATIONS ==========
    post {
        success {
            echo '✅ Pipeline réussi !'

            emailext subject: "✅ Build Réussi - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                     body: """
                     <h2>Build ${env.BUILD_NUMBER} déployé avec succès !</h2>

                     <p><strong>Projet :</strong> ${env.JOB_NAME}</p>
                     <p><strong>Branche :</strong> ${env.GIT_BRANCH}</p>
                     <p><strong>Durée :</strong> ${currentBuild.durationString}</p>

                     <p><a href="${env.BUILD_URL}">Voir le build</a></p>
                     """,
                     to: 'votre-email@example.com',
                     mimeType: 'text/html'
        }

        failure {
            echo '❌ Pipeline échoué !'

            emailext subject: "❌ Build Échoué - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                     body: """
                     <h2>Le build ${env.BUILD_NUMBER} a échoué !</h2>

                     <p><strong>Projet :</strong> ${env.JOB_NAME}</p>
                     <p><strong>Branche :</strong> ${env.GIT_BRANCH}</p>
                     <p><strong>Erreur :</strong> ${currentBuild.result}</p>

                     <p><a href="${env.BUILD_URL}console">Voir les logs</a></p>
                     """,
                     to: 'votre-email@example.com',
                     mimeType: 'text/html'
        }

        always {
            echo '🧹 Nettoyage...'
        }
    }
}