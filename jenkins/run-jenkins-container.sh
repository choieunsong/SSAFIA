docker run -d -u root -p 9090:8080 --name=jenkins \
-v ./jenkins_home:/var/jenkins_home \
-v /var/run/docker.sock:/var/run/docker.sock \
jenkins_jenkins/latest
