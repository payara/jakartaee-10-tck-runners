#!/bin/bash

PAYARA_HOME=$1
STATUS=$($PAYARA_HOME/bin/asadmin list-domains | grep "domain1 running")

if [ -z "$STATUS" ]; then
    echo "Domain1 is not running. Starting domain1..."
    $PAYARA_HOME/bin/asadmin start-domain domain1
else
    echo "Domain1 is already running."
fi
