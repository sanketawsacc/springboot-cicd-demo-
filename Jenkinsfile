
pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                checkout scm
                echo 'Code checked out successfully'
            }
        }

        stage('Docker Check') {
            steps {
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

        stage('Docker Build') {
            steps {
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
                        set AWS_DEFAULT_REGION=ap-southeast-2

                        "C:/Program Files/Amazon/AWSCLIV2/aws.exe" ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 448678333371.dkr.ecr.ap-southeast-2.amazonaws.com

                        docker tag springboot-cicd-demo:1.2 448678333371.dkr.ecr.ap-southeast-2.amazonaws.com/springboot-cicd-demo:1.2

                        docker push 448678333371.dkr.ecr.ap-southeast-2.amazonaws.com/springboot-cicd-demo:1.2
                    '''
                }
            }
        }

        stage('SSM Connectivity Test') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'aws-ecr-credentials',
                    usernameVariable: 'AWS_ACCESS_KEY_ID',
                    passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                )]) {
                    bat '''
                        set AWS_DEFAULT_REGION=ap-southeast-2

                        "C:/Program Files/Amazon/AWSCLIV2/aws.exe" ssm describe-instance-information --filters "Key=InstanceIds,Values=i-00c42fb63c9f72d41" --query "InstanceInformationList[*].[InstanceId,PingStatus]" --output table
                    '''
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'aws-ecr-credentials',
                    usernameVariable: 'AWS_ACCESS_KEY_ID',
                    passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                )]) {
                    powershell '''
                        $region = "ap-southeast-2"
                        $instanceId = "i-00c42fb63c9f72d41"
                        $image = "448678333371.dkr.ecr.ap-southeast-2.amazonaws.com/springboot-cicd-demo:1.2"

                        $commands = @(
                            "docker pull $image",
                            "docker rm -f springboot-demo || true",
                            "docker run -d --restart unless-stopped --name springboot-demo -p 4000:4000 $image",
                            "docker ps"
                        )

                        $parameters = @{
                            commands = $commands
                        } | ConvertTo-Json -Compress

                        $commandId = aws ssm send-command `
                            --region $region `
                            --instance-ids $instanceId `
                            --document-name "AWS-RunShellScript" `
                            --parameters $parameters `
                            --comment "Jenkins EC2 deployment" `
                            --query "Command.CommandId" `
                            --output text

                        Write-Host "SSM Command ID: $commandId"

                        if (!$commandId -or $commandId -eq "None") {
                            throw "Failed to send deployment command"
                        }

                        do {
                            Start-Sleep -Seconds 5

                            $status = aws ssm get-command-invocation `
                                --region $region `
                                --command-id $commandId `
                                --instance-id $instanceId `
                                --query "Status" `
                                --output text

                            Write-Host "Deployment status: $status"

                        } while ($status -in @("Pending", "InProgress", "Delayed"))

                        if ($status -ne "Success") {
                            throw "EC2 deployment failed with status: $status"
                        }

                        $output = aws ssm get-command-invocation `
                            --region $region `
                            --command-id $commandId `
                            --instance-id $instanceId `
                            --query "StandardOutputContent" `
                            --output text

                        Write-Host "EC2 Deployment Output:"
                        Write-Host $output
                    '''
                }
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
