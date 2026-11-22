
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

        stage('Push to ECR') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'aws-ecr-credentials',
                    usernameVariable: 'AWS_ACCESS_KEY_ID',
                    passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                )]) {
                    bat '''
                        set AWS_ACCESS_KEY_ID=%AWS_ACCESS_KEY_ID%
                        set AWS_SECRET_ACCESS_KEY=%AWS_SECRET_ACCESS_KEY%
                        set AWS_DEFAULT_REGION=ap-southeast-2

                        aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 448678333371.dkr.ecr.ap-southeast-2.amazonaws.com

                        docker tag springboot-cicd-demo:1.2 448678333371.dkr.ecr.ap-southeast-2.amazonaws.com/springboot-cicd-demo:1.2

                        docker push 448678333371.dkr.ecr.ap-southeast-2.amazonaws.com/springboot-cicd-demo:1.2
                    '''
                }
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
