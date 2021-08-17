docker run -d -u root -p 9090:8080 --name=jenkins \
-v /var/jenkins_home:/var/jenkins_home \
-v /var/run/docker.sock:/var/run/docker.sock \
-v /usr/bin/docker:/usr/bin/docker \
-v /usr/bin/docker-compose:/usr/bin/docker-compose \
jenkins/jenkins
