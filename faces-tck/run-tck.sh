#!/bin/bash
if [ JAVA_HOME = "" ] ; then
    export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
fi

rm -r target
mkdir target
if [ ! -f target/jakarta-faces-tck-4.0.3.zip ]; then
    echo "Downloading jakarta-faces-tck-4.0.3.zip"
    wget -q https://download.eclipse.org/jakartaee/faces/4.0/jakarta-faces-tck-4.0.3.zip -O target/jakarta-faces-tck-4.0.3.zip
fi
echo "Unzipping"
rm -rf target/faces-tck-4.0.3
unzip -q target/jakarta-faces-tck-4.0.3.zip -d target

echo "Running TCK"
# add payara profile, should be in the next version of upstream pom.xml
cp pom.xml-tck target/faces-tck-4.0.3/tck/pom.xml
# change usage of glassfish with payara
cp pom.xml-old-tck-run target/faces-tck-4.0.3/tck/old-tck/run/pom.xml
#mvn verify -P payara-ci-remote,\!glassfish-ci-managed -Dglassfish.version=6.2022.1.Alpha5-SNAPSHOT -f target/faces-tck-4.0.3/tck/pom.xml -pl old-tck -amd
mvn verify -P payara-ci-remote,\!glassfish-ci-managed -Dglassfish.version=6.2022.1.Alpha4 -f target/faces-tck-4.0.3/tck/pom.xml
