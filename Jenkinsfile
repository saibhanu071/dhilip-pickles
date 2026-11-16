
pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo 'GitHub checkout successful'
            }
        }

        stage('Build') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw -B clean verify'
            }
        }

        stage('SonarCloud Analysis') {
            steps {
                withSonarQubeEnv('sonarcloud') {
                    sh '''
                        ./mvnw -B org.sonarsource.scanner.maven:sonar-maven-plugin:sonar \
                        -Dsonar.projectKey=saibhanu071_dhilip-pickles \
                        -Dsonar.organization=saibhanu071
                    '''
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t dhilip-pickles:latest .'
            }
        }

        stage('Trivy Security Scan') {
            steps {
                sh '''
                    mkdir -p "$HOME/trivy-tmp"
                    TMPDIR="$HOME/trivy-tmp" trivy image \
                    --exit-code 1 \
                    --severity HIGH,CRITICAL \
                    dhilip-pickles:latest
                '''
            }
        }
    }

    post {
        success {
            echo 'Build, SonarCloud, Docker and Trivy scan successful!'
        }
        failure {
            echo 'Pipeline failed. Check console output.'
        }
    }
}

