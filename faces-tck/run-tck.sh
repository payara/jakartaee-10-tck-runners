#!/bin/bash
if [ JAVA_HOME = "" ] ; then
    export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
fi

rm -r target
mkdir target
wget -q https://download.eclipse.org/ee4j/jakartaee-tck/jakartaee10/staged/eftl/jakarta-faces-tck-4.0.1.zip -O target/jakarta-faces-tck-4.0.1.zip
unzip -q target/jakarta-faces-tck-4.0.1.zip -d target
rm target/faces-tck-4.0.1/tck
cp pom.xml-works target/faces-tck-4.0.1/tck/pom.xml
mvn clean install -P payara-ci-remote,\!glassfish-ci-managed -Dglassfish.version=7.0.0-M4 -f target/faces-tck-4.0.1/tck/pom.xml
