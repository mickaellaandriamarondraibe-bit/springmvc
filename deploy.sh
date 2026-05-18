#!/bin/bash

set -e

APP_NAME="forage"
TOMCAT_HOME="/opt/tomcat"

echo "1) Nettoyage et compilation Maven..."
mvn clean package

echo "2) Suppression de l'ancienne application..."
rm -rf "$TOMCAT_HOME/webapps/$APP_NAME"
rm -f "$TOMCAT_HOME/webapps/$APP_NAME.war"

echo "3) Copie du nouveau WAR vers Tomcat..."
cp "target/$APP_NAME.war" "$TOMCAT_HOME/webapps/"

echo "Deploiement termine."
echo "URL probable : http://localhost:8080/$APP_NAME/"
