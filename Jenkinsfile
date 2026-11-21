
pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                echo 'Code checked out successfully'
            }
        }

        stage('Docker Check'){
            steps{
                bat 'docker version'
            }
        }

        stage('Build') {
            steps {
                bat 'mvn clean compile'
            }
        }
        stage('Test') {
            steps {
                bat 'mvn test'
            }
        }

        stage('Package') {
            steps {
                bat 'mvn package -DskipTests'
            }
        }

        stage('Docker Build'){
            steps{
                bat 'docker build -t springboot-cicd-demo:1.2 .'
            }
        }

        stage('Deploy') {
            steps {
                bat '''
                    docker rm -f springboot-demo 2>NUL || echo No previous container found
                    docker run -d --name springboot-demo -p 8082:4000 springboot-cicd-demo:1.2
                '''
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar',
                                 fingerprint: true
            }
        }
    }
}
