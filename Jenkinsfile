
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

        stage('Push to AWS ECR') {
            steps {
                sh '''
                    aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 173500116403.dkr.ecr.ap-southeast-2.amazonaws.com

                    docker tag dhilip-pickles:latest 173500116403.dkr.ecr.ap-southeast-2.amazonaws.com/dhilip-pickles:latest

                    docker push 173500116403.dkr.ecr.ap-southeast-2.amazonaws.com/dhilip-pickles:latest
                '''
            }
        }
    }

    post {
        success {
            echo 'Build, SonarCloud, Docker, Trivy and ECR push successful!'
        }
        failure {
            echo 'Pipeline failed. Check console output.'
        }
    }
}

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

        stage('Push to AWS ECR') {
            steps {
                sh '''
                    aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 173500116403.dkr.ecr.ap-southeast-2.amazonaws.com

                    docker tag dhilip-pickles:latest 173500116403.dkr.ecr.ap-southeast-2.amazonaws.com/dhilip-pickles:latest

                    docker push 173500116403.dkr.ecr.ap-southeast-2.amazonaws.com/dhilip-pickles:latest
                '''
            }
        }
    }

    post {
        success {
            echo 'Build, SonarCloud, Docker, Trivy and ECR push successful!'
        }
        failure {
            echo 'Pipeline failed. Check console output.'
        }
    }
}

