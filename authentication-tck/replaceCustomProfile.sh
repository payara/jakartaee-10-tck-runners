#!/bin/bash
sed -i "/<id>custom<\/id>/r payara-profile.xml" target/authentication-tck-3.1.0/tck/pom.xml
