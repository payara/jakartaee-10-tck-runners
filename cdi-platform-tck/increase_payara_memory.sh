#!/bin/bash

PAYARA_HOME=$1
DOMAIN_NAME="domain1"
NEW_JVM_OPTION="-Xmx2048m"

STATUS=$($PAYARA_HOME/bin/asadmin list-domains | grep "$DOMAIN_NAME running")
if [ -z "$STATUS" ]; then
    echo "Domain $DOMAIN_NAME is not running. Starting $DOMAIN_NAME..."
    $PAYARA_HOME/bin/asadmin start-domain $DOMAIN_NAME
else
    echo "Domain $DOMAIN_NAME is already running."
fi

EXISTING_OPTIONS=$($PAYARA_HOME/bin/asadmin list-jvm-options | grep -E '-Xmx[0-9]+m')
if [ -n "$EXISTING_OPTIONS" ]; then
    echo "Existing JVM options found: $EXISTING_OPTIONS. Deleting..."
    for OPTION in $EXISTING_OPTIONS; do
        $PAYARA_HOME/bin/asadmin delete-jvm-options "$OPTION"
    done
fi

echo "Adding new JVM option: $NEW_JVM_OPTION..."
$PAYARA_HOME/bin/asadmin create-jvm-options "$NEW_JVM_OPTION"

echo "Stopping $DOMAIN_NAME..."
$PAYARA_HOME/bin/asadmin stop-domain $DOMAIN_NAME
