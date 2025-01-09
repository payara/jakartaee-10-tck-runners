#!/bin/bash
sed -i -e "/<\/profiles>/r payara-profiles.xml" -e "/<\/profiles>/d" target/authentication-tck-3.1.0/tck/pom.xml

# Not sure if this is the "correct" way to configure this, but this Service Loader is a GlassFish specific class (as in "GlassFish" GlassFish: it's not a part of Metro)
# The class name was changed from what we have in Payara in two commits: https://github.com/eclipse-ee4j/glassfish/commit/0dff8ac0607dbbee94d16d4e7da5c4618b61d295 and https://github.com/eclipse-ee4j/glassfish/commit/5da439185d5d7be1f61fcc201ae3ab53f4fd6a23
echo "com.sun.enterprise.security.webservices.ClientPipeCreator" > target/authentication-tck-3.1.0/tck/spi/soap/src/main/resources/META-INF/services/com.sun.xml.ws.assembler.metro.dev.ClientPipelineHook

# Spi/servlet unit tests leave a log file locked, blocking the next tests to progress
cp ./tck-to-replace/spi/servlet/src/test/java/ee/jakarta/tck/authentication/test/basic/ServletUnitTest.java ./target/authentication-tck-3.1.0/tck/spi?servlet/test/java/ee/jakarta/tck/authentication/test/basic/ServletUnitTest.java -force
