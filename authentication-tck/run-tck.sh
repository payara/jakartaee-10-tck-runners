#!/bin/bash
if [ JAVA_HOME = "" ] ; then
    export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
fi

rm -r target
mkdir target
wget -q https://download.eclipse.org/ee4j/jakartaee-tck/jakartaee10/staged/eftl/jakarta-authentication-tck-3.0.1.zip -O target/jakarta-authentication-tck-3.0.1.zip
unzip -q target/jakarta-authentication-tck-3.0.1.zip -d target
sed -i "/<id>custom<\/id>/r payara-profile.xml" target/authentication-tck-3.0.1/tck/pom.xml
mvn clean install -Pcustom -f target/authentication-tck-3.0.1/tck/pom.xml