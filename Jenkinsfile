
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
                        ./mvnw sonar:sonar \
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
    }

    post {
        success {
            echo 'Build, SonarCloud analysis and Docker image creation successful!'
        }
        failure {
            echo 'Pipeline failed. Check console output.'
        }
    }
}

