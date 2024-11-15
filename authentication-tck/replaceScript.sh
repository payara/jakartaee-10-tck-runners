#!/bin/bash
sed -i "/<\/profiles>/r payara-profiles.xml" target/authentication-tck-3.1.0/tck/pom.xml
