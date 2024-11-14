#!/bin/bash
sed -i "/<id>custom<\/id>/r payara-profile.xml" target/authentication-tck-${VERSION}/tck/pom.xml