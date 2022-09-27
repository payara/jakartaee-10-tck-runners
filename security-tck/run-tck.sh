#!/bin/bash
if [ JAVA_HOME = "" ] ; then
    export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
fi

rm -r target
mkdir target
wget -q https://download.eclipse.org/ee4j/jakartaee-tck/jakartaee10/staged/eftl/jakarta-security-tck-3.0.0.zip -O target/jakarta-security-tck-3.0.0.zip
unzip -q target/jakarta-security-tck-3.0.0.zip -d target
cp -f payara-profile.xml target/security-tck-3.0.0/tck/pom.xml
cp -f old-tck-run-payara.xml target/security-tck-3.0.0/tck/old-tck/run/pom.xml

if [[ "$PROFILE" == "web" || "$PROFILE" == "WEB" ]];then
  mvn clean install help:active-profiles -Ppayara-ci-managed,web -f target/security-tck-3.0.0/tck/pom.xml
else
  mvn clean install help:active-profiles -Ppayara-ci-managed -f target/security-tck-3.0.0/tck/pom.xml
fi

