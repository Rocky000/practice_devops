pipeline {
    agent any

    environment {
        REPO_SLUG = "practice_devops/restapi_deploy"
        VERSION = sh(script: 'git describe --tags --abbrev=0', returnStdout: true).trim()
        KUBE_CONFIG = credentials('jenkins-cred-id-k8s-config-file')
        KUBE_NAMESPACE = 'your-namespace'
    }                  

    parameters {
        choice (
            choices: ['dev', 'test'], 
            description: 'Select Deployment Environment', 
            name: 'DEPLOY_TO'
        )
    string (
            defaultValue: '',
            name : 'BranchName'
        )
    }

    stages{
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Git Clone') {
            steps {
                script {
                    if( BranchName != null && !BranchName.isEmpty() && DEPLOY_TO == 'dev' ){
                        branchName = BranchName
                        println ("Getting branch name "+branchName)
                    }
                    else if( DEPLOY_TO == "dev"){
                        branchName="development"
                        println ("Getting branch name "+branchName)
                    }else if (DEPLOY_TO == 'test' && BranchName.isEmpty() ){
                        println("you have empty release candidate name.Please put the release candidate branch name")
                        error("Error: you have empty release candidate name.Please put the release candidate branch name.")
                        assert false
                    }else if (DEPLOY_TO == 'test' && !BranchName.isEmpty() ){
                        branchName = BranchName
                        println ("Getting branch name "+branchName)
                         
                    }else{
                        println "Invalid environment"
                    }
                    println ("Getting branch name "+branchName)

                    gitUtils.gitClone("git@github.com:Rocky000/${env.REPO_SLUG}.git" , branchName)
                }
            }
        }
        stage('Docker image Build and Push') {
            steps {
                script {
                    if (DEPLOY_TO == 'dev' || DEPLOY_TO == 'test'){
                        try {
                            sh label: 'Docker build', script: "docker build -t Rocky000/${env.REPO_SLUG}:$VERSION ."
                        }
                        catch (exc) {
                            error("Error: Issue in building docker images.")
                            assert false
                        }
                        try {
                            sh label: 'docker push', script: "docker push Rocky000/${env.REPO_SLUG}:$VERSION"
                        }
                        catch (exc) {
                            error("Error: Issue in pushing images.")
                            assert false
                        }
                    }else{
                        println("Invalid Environment")
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                script {
                    withCredentials([file(credentialsId: 'kubernetes-config-file', variable: 'KUBE_CONFIG')]) {
                        sh "export KUBECONFIG=$KUBE_CONFIG"
                    }

                    sh "sed -i 's|{{IMAGE_NAME}}|Rocky000/${env.REPO_SLUG}|g' your-kubernetes-yaml"
                    sh "sed -i 's|{{IMAGE_VERSION}}|$VERSION|g' your-kubernetes-yaml"

                    sh "kubectl apply -n $KUBE_NAMESPACE -f your-kubernetes-yaml"
                }
            }
        }  
    }
    
    post {
        success {
            echo 'Deployment successful!'
        }
        failure {
            echo 'Deployment failed!'
        }
    }
}   
