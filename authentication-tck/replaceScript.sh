#!/bin/bash
sed -i -e "/<\/profiles>/r payara-profiles.xml" -e "/<\/profiles>/d" target/authentication-tck-3.1.0/tck/pom.xml