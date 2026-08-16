pipeline {
    agent any

    environment {
        SONAR_PROJECT_KEY = 'fraud-detection-platform'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build Backend') {
            steps {
                sh 'mvn -B -U -DskipTests clean package'
            }
        }

        stage('Test Backend') {
            steps {
                sh 'mvn -B test'
            }
            post {
                always {
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('local-sonarqube') {
                    sh "mvn -B org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY}"
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh 'npm ci'
                    sh 'npm run build -- --configuration production'
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline termine avec succes : backend compile, teste, analyse par SonarQube, frontend compile.'
        }
        failure {
            echo 'Le pipeline a echoue - consultez les logs de la stage en erreur ci-dessus.'
        }
    }
}
