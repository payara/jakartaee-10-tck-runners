#!/bin/bash
if [ JAVA_HOME = "" ] ; then
    export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
fi

rm -r target
mkdir target
wget -q https://download.eclipse.org/ee4j/jakartaee-tck/jakartaee10/staged/eftl/jakarta-security-tck-3.0.0.zip -O target/jakarta-security-tck-3.0.0.zip
unzip -q target/jakarta-security-tck-3.0.0.zip -d target
cp -f payara-profile.xml target/security-tck-3.0.0/tck/pom.xml
mvn clean install -Ppayara-ci-managed -pl '!old-tck,!old-tck/build,!old-tck/run' -f target/security-tck-3.0.0/tck/pom.xml -Dglassfish.version=7.0.0-M4