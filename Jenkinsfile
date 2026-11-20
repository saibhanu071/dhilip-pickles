pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        IMAGE_REPO = 'saibhanu071/dhilip-pickles'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo 'GitHub checkout successful'
            }
        }

        stage('Maven Build and Unit Tests') {
            steps {
                sh '''
                    chmod +x mvnw
                    ./mvnw -B clean verify
                '''
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

        stage('Generate Image Tag') {
            steps {
                script {
                    env.IMAGE_TAG = sh(
                        script: 'git rev-parse --short=12 HEAD',
                        returnStdout: true
                    ).trim()
                }
                echo "Docker image tag: ${IMAGE_TAG}"
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    docker build \
                      -t "$IMAGE_REPO:$IMAGE_TAG" \
                      .
                '''
            }
        }

        stage('Trivy Security Scan') {
            steps {
                sh '''
                    mkdir -p "$HOME/trivy-tmp"
                    TMPDIR="$HOME/trivy-tmp" trivy image \
                      --exit-code 1 \
                      --severity HIGH,CRITICAL \
                      "$IMAGE_REPO:$IMAGE_TAG"
                '''
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'c962e756-b880-47ba-85dc-7d377bd0415d',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_TOKEN'
                )]) {
                    sh '''
                        set -e
                        trap 'docker logout >/dev/null 2>&1 || true' EXIT

                        echo "$DOCKER_TOKEN" | docker login \
                          --username "$DOCKER_USER" \
                          --password-stdin

                        docker tag "$IMAGE_REPO:$IMAGE_TAG" \
                          "$IMAGE_REPO:latest"

                        docker push "$IMAGE_REPO:$IMAGE_TAG"
                        docker push "$IMAGE_REPO:latest"
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "SUCCESS: ${IMAGE_REPO}:${IMAGE_TAG} pushed to Docker Hub!"
        }
        failure {
            echo 'Pipeline failed. Check Jenkins Console Output.'
        }
    }
}
