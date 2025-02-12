#!/bin/bash

#function to parse an xml file. Returns $E and $C, $E being the Entity, $C the Content 
rdom () { local IFS=\> ; read -d \< E C ;}

#Find every failsafe-summary.xml in the child folders and list them in a file 
find ${0%/*} -iname 'failsafe-summary.xml' -type f > target/summarylist.txt
COMPLETED=0
ERRORS=0
FAILURES=0
SKIPPED=0

# for each element of the file list, parse them to find the number of completed/errors/failures/skipped tests
while read File; 
do
    while rdom;
    do
        if [[ $E = "completed" ]]; then
            COMPLETED=$(($COMPLETED + $C))
        elif [[ $E = "errors" ]]; then
            ERRORS=$(($ERRORS + $C))
        elif [[ $E = "failures" ]]; then
            FAILURES=$(($FAILURES + $C))
        elif [[ $E = "skipped" ]]; then
            SKIPPED=$(($SKIPPED + $C))
        fi
    done < $File
done < ${0%/*}/target/summarylist.txt

#Create a specific failsafe-summary-custom.xml to aggregate the results in one file
echo '<?xml version="1.0" encoding="UTF-8"?>' > ${0%/*}/target/failsafe-summary-custom.xml
echo '<failsafe-summary xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="https://maven.apache.org/surefire/maven-surefire-plugin/xsd/failsafe-summary.xsd" result="254" timeout="false">' >> ${0%/*}/target/failsafe-summary-custom.xml
echo '    <completed>'$COMPLETED'</completed>'  >> ${0%/*}/target/failsafe-summary-custom.xml
echo '    <errors>'$ERRORS'</errors>' >> ${0%/*}/target/failsafe-summary-custom.xml
echo '    <failures>'$FAILURES'</failures>' >> ${0%/*}/target/failsafe-summary-custom.xml
echo '    <skipped>'$SKIPPED'</skipped>' >> ${0%/*}/target/failsafe-summary-custom.xml
echo '    <failureMessage xsi:nil="true" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>' >> ${0%/*}/target/failsafe-summary-custom.xml
echo '</failsafe-summary>' >> ${0%/*}/target/failsafe-summary-custom.xml

#Clean the list of files
rm ${0%/*}/target/summarylist.txt