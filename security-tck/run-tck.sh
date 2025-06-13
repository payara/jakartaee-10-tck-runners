#!/bin/bash
if [ JAVA_HOME = "" ] ; then
    export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
fi

rm -r target
mkdir target
wget -q https://download.eclipse.org/jakartaee/security/4.0/jakarta-security-tck-4.0.0.zip -O target/jakarta-security-tck-4.0.0.zip
unzip -q target/jakarta-security-tck-4.0.0.zip -d target
sed -i 's/\${glassfish.root}\/glassfish8/\${payara.root}\/payara7/g' target/security-tck-4.0.0/tck/**/pom.xml
sed -i 's#<javax.net.ssl.trustStore>\${payara.root}/payara7/glassfish/domains/domain1/config/cacerts.jks</javax.net.ssl.trustStore>#<javax.net.ssl.trustStore>\${payara.root}/payara7/glassfish/domains/domain1/config/cacerts.p12</javax.net.ssl.trustStore>\n<javax.net.ssl.trustStorePassword>changeit</javax.net.ssl.trustStorePassword>\n<javax.net.ssl.trustStoreType>PKCS12</javax.net.ssl.trustStoreType>#g' target/security-tck-4.0.0/tck/**/pom.xml
sed -i 's/cacerts.jks/cacerts.p12/g' target/security-tck-4.0.0/tck/**/pom.xml
sed -i 's/<alias>tomcat<\/alias>/<alias>tomcat-openid3<\/alias>/g' target/security-tck-4.0.0/tck/app-openid3/pom.xml
cp -f payara-profile.xml target/security-tck-4.0.0/tck/pom.xml
cp -f old-tck-run-payara.xml target/security-tck-4.0.0/tck/old-tck/run/pom.xml

if [[ "$PROFILE" == "web" || "$PROFILE" == "WEB" ]];then
  mvn clean install help:active-profiles -Ppayara-ci-managed,web -f target/security-tck-4.0.0/tck/pom.xml -Dfailsafe.rerunFailingTestsCount=2
else
  mvn clean install help:active-profiles -Ppayara-ci-managed -f target/security-tck-4.0.0/tck/pom.xml -Dfailsafe.rerunFailingTestsCount=2
fi

