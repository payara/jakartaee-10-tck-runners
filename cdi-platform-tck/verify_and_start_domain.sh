#!/bin/bash
STATUS=$(${payara.home}/bin/asadmin list-domains | grep "domain1 running")

if [ -z "$STATUS" ]; then
    echo "Domain1 is not running. Starting domain1..."
    ${payara.home}/bin/asadmin start-domain domain1
else
    echo "Domain1 is already running."
fi
