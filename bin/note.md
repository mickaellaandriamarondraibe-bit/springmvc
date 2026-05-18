run.sh      → compiler le projet et lancer Tomcat
deploy.sh   → compiler le WAR et le copier dans Tomcat
mvn package && sudo rm -rf /opt/tomcat/webapps/forage /opt/tomcat/webapps/forage.war /opt/tomcat/work/Catalina/localhost/forage && sudo cp target/forage.war /opt/tomcat/webapps/ && sudo /opt/tomcat/bin/shutdown.sh || true; sudo /opt/tomcat/bin/startup.sh
