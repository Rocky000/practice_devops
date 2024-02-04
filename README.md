# REST API
Here I am using the FASTAPI for the task.
# prerequisites:
  * python3
  * python3-pip
  * terraform
  * docker
    
# Step 1: Install tools needed
 -     $ pip install "fastapi[all]"
 This command will install the fastapi with dependencies.
 
 -     $ sudo apt install uvicorn
 This command will instal the uvicorn to the run on the local machine.

# Step 2: Witre the code in python3
Here I have create the code for the API exposing data tasks, it can be found in the following 
-     File path:"./practice_devops/restapi_deploy/fastapi_app.py".

After succesfully running the code we can see the expected values from the following links:
 
    1. http://<localhost/IP>:8000/weather
    2. http://<localhost/IP>:8000/health
    3. http://<localhost/IP>:8000/docs

# Step 3: Witre the Dockerfile for the APP
Here I have create Dockerfile for the containerization for the APP, it can be found in the following 
-     File path:"./practice_devops/restapi_deploy/Dockerfile".
Command to run the docker:
-     $ docker run -p 8000:8000 -e API_KEY='api_key_of_3rd_party_soft' <image-name>:<tag>

# Step 4: Created a terraform script for the Kubernetes EKS cluster deployment on AWS
It can be found in the following path "./practice_devops/Eks_deploy_Terraform".

Please run the command before starting - 
-     $ terraform init //to initate the terraform and installation of the dependecies for the providers
-     $ terraform plan //to check the terraform formates 
-     $ terraform apply //to actually create the EKS cluster
-     $ terraform destory //to destory anything created by this terraform script

# Note: I could not actually apply the  "terrafrom apply" command here as i don't have any personal AWS account yet.

# Step 5: Create a Jenkins CD pipeline groovy script
This script is used to build docker image and push it to docker registry, versioning of the image and deploy it on k8s cluster environments.
for k8s deployment I should have added the helm chart for clear view of the task but unfortunately, couldn't due to some emergecies. 
-     File path: "./practice_devops/restapi_deploy/pipelines/weather_data_cd.groovy"

# System Design
The Solution of the Part: B is in the following 
-     File path:"./practice_devops/cloud_architecture_ecommerce_application.png".

Here I have create design for e-commerce application which is suppose to handle 1 million requests.
I have used PostgreSQL EC2 server for database for cost management but Ideal case is to use RDS service of the AWS.
The k8s pods are spot instances, we can auto-scale this pods as per demands.
I have only exposed the fronted UI publically in this model.
