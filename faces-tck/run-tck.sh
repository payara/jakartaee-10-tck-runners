#!/bin/bash
#if [ JAVA_HOME = "" ] ; then
    export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
#fi

#echo "Downloading Faces TCK"
#cd ..
#mvn clean install -pl .,:tck-download,:jakarta-faces-tck
#cd faces-tck

#echo "Building old tck, expected time 3-7 minutes"
#cd target/faces-tck-4.1.1/tck/old-tck
#mvn clean install -Dtck.mode=platform -Dmojarra.noupdate=true -Dpayara.version=7.2024.1.Alpha2 -Dfaces.version=4.1.1 -pl :old-faces-tck-parent,:old-tck-build | tee ../../../old-tck.log
#cd ..


mvn clean verify
