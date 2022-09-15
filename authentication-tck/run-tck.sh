#!/bin/bash
if [ JAVA_HOME = "" ] ; then
    export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
fi
VERSION=3.0.1

rm -r target
mkdir target
wget -q https://download.eclipse.org/jakartaee/authentication/3.0/jakarta-authentication-tck-${VERSION}.zip -O target/jakarta-authentication-tck-${VERSION}.zip
unzip -q target/jakarta-authentication-tck-${VERSION}.zip -d target
sed -i "/<id>custom<\/id>/r payara-profile.xml" target/authentication-tck-${VERSION}/tck/pom.xml
cp pom-old-tck-run.xml target/authentication-tck-${VERSION}/tck/old-tck/run/pom.xml
mvn clean test surefire-report:report -Pcustom,old-tck,platform,\!glassfish-ci-managed -f target/authentication-tck-${VERSION}/tck/pom.xml -Dglassfish.version=6.2022.1.Alpha5-SNAPSHOT
