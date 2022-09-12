#!/bin/bash
if [ JAVA_HOME = "" ] ; then
    export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
fi

#rm -r target
#mkdir target
if [ ! -f target/jakarta-faces-tck-4.0.1.zip ]; then
    echo "Downloading jakarta-faces-tck-4.0.1.zip"
    wget -q https://download.eclipse.org/ee4j/jakartaee-tck/jakartaee10/staged/eftl/jakarta-faces-tck-4.0.1.zip -O target/jakarta-faces-tck-4.0.1.zip
fi
echo "Unzipping"
rm -rf target/faces-tck-4.0.1
unzip -q target/jakarta-faces-tck-4.0.1.zip -d target

echo "Running TCK"
# add payara profile, should be in the next version of upstream pom.xml
cp pom.xml-tck target/faces-tck-4.0.1/tck/pom.xml
# change usage of glassfish with payara
cp pom.xml-old-tck-run target/faces-tck-4.0.1/tck/old-tck/run/pom.xml
#mvn verify -P payara-ci-remote,\!glassfish-ci-managed -Dglassfish.version=6.2022.1.Alpha5-SNAPSHOT -f target/faces-tck-4.0.1/tck/pom.xml -pl old-tck -amd
mvn verify -P payara-ci-remote,\!glassfish-ci-managed -Dglassfish.version=6.2022.1.Alpha4 -f target/faces-tck-4.0.1/tck/pom.xml
