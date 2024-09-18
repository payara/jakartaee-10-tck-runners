#!/bin/bash

# Check if payara.home is passed as a parameter in the format payara.home=value
if [[ "$1" != payara.home=* ]]; then
    echo "Usage: $0 payara.home=<PAYARA_PATH>/appserver/distributions/payara/target/stage/payara7"
    exit 1
fi

# Extract the value of payara.home from the argument
PAYARA_HOME="${1#payara.home=}"

# Ensure JAVA_HOME is set
if [ -z "$JAVA_HOME" ]; then
    export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-arm64
fi

# Remove target directory if it exists
rm -rf target
mkdir target

# Download the file and check if successful
wget -q https://download.eclipse.org/jakartaee/persistence/3.2/jakarta-persistence-tck-3.2.0.zip -O target/jakarta-persistence-tck-3.2.0.zip

if [ ! -f target/jakarta-persistence-tck-3.2.0.zip ]; then
    echo "Download TCK failed!"
    exit 1
fi

# Unzip only if download succeeded
unzip -q target/jakarta-persistence-tck-3.2.0.zip -d target

# Install TCK dependencies
cd target/persistence-tck/artifacts/ || exit
bash artifact-install.sh

# Copy the Payara profile
cd ../../../
cp -f payara-profile.xml target/persistence-tck/bin/pom.xml
echo "Copied payara-profile.xml to target/persistence-tck/bin/pom.xml"

# Execute TCK
cd target/persistence-tck || exit
mvn -f bin/pom.xml clean verify -P staging,full,derby,eclipselink -Dpayara.home=$PAYARA_HOME -Declipselink.version=5.0.0-B03 -Declipselink.asm.version=9.7.0 -Dtck.version=3.2.0