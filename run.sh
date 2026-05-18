#!/bin/bash

set -e

TOMCAT_HOME="/opt/tomcat"

./deploy.sh

echo "Redemarrage Tomcat..."
"$TOMCAT_HOME/bin/shutdown.sh" || true
sleep 2
"$TOMCAT_HOME/bin/startup.sh"

echo "Projet lance."
echo "URL : http://localhost:8080/forage/"
