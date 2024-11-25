#!/bin/bash
if [ JAVA_HOME = "" ] ; then
    export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
fi

echo "Downloading Faces TCK"
cd ..
mvn clean install -pl .,:tck-download,:jakarta-faces-tck
cd faces-tck

mvn clean verify
