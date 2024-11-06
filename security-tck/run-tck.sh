#!/bin/bash
if [ JAVA_HOME = "" ] ; then
    export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
fi

rm -r target
mkdir target
wget -q https://download.eclipse.org/jakartaee/security/3.0/jakarta-security-tck-3.0.0.zip -O target/jakarta-security-tck-3.0.0.zip
unzip -q target/jakarta-security-tck-3.0.0.zip -d target
cp -f payara-profile.xml target/security-tck-3.0.0/tck/pom.xml
cp -f old-tck-run-payara.xml target/security-tck-3.0.0/tck/old-tck/run/pom.xml
cp -f web.xml target/security-tck-3.0.0/tck/old-tck/source/src/com/sun/ts/tests/securityapi/ham/customform/base/securityapi_ham_customform_base_web.xml

if [[ "$PROFILE" == "web" || "$PROFILE" == "WEB" ]];then
  mvn clean install help:active-profiles -Ppayara-ci-managed,web -f target/security-tck-3.0.0/tck/pom.xml
else
  mvn clean install help:active-profiles -Ppayara-ci-managed -f target/security-tck-3.0.0/tck/pom.xml
fi

