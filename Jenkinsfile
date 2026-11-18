
pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'dhilip-pickles'
        DOCKER_TAG = 'latest'
    }

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
                sh 'docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .'
            }
        }

        stage('Trivy Security Scan') {
            steps {
                sh '''
                    mkdir -p "$HOME/trivy-tmp"
                    TMPDIR="$HOME/trivy-tmp" trivy image \
                    --exit-code 1 \
                    --severity HIGH,CRITICAL \
                    ${DOCKER_IMAGE}:${DOCKER_TAG}
                '''
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_TOKEN'
                )]) {
                    sh '''
                        echo "$DOCKER_TOKEN" | docker login \
                            -u "$DOCKER_USER" --password-stdin

                        docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} \
                            "$DOCKER_USER/${DOCKER_IMAGE}:${DOCKER_TAG}"

                        docker push "$DOCKER_USER/${DOCKER_IMAGE}:${DOCKER_TAG}"

                        docker logout
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Build, SonarCloud, Docker, Trivy and Docker Hub push successful!'
        }
        failure {
            echo 'Pipeline failed. Check console output.'
        }
    }
}
