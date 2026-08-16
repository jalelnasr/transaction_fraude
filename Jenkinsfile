pipeline {
    agent any

    environment {
        SONAR_PROJECT_KEY = 'fraud-detection-platform'
        // Le reseau Docker de cette machine devient instable quand Maven telecharge
        // beaucoup de dependances en parallele (plusieurs echecs de transfert
        // constates). On force un telechargement sequentiel, plus lent mais fiable.
        MAVEN_OPTS = '-Daether.connector.basic.threads=1'
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
                    // npm n'a pas d'equivalent fiable au reglage qui a corrige Maven
                    // (limiter les connexions simultanees). Le reseau Docker de cette
                    // machine etant instable par intermittence, on retente jusqu'a
                    // 3 fois : node_modules deja telecharge est conserve entre les
                    // tentatives, donc chaque nouvel essai part avec moins de travail
                    // restant, comme observe manuellement avec Maven.
                    sh 'npm config set fetch-retries 5'
                    sh 'npm config set fetch-retry-mintimeout 20000'
                    sh 'npm config set fetch-timeout 300000'
                    retry(3) {
                        sh 'npm ci'
                    }
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
