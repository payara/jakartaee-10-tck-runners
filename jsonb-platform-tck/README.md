# Jakarta JSONB Extra Platform TCK

## Prerequisite
Install the Platform TCK via tck-download module, since the ones published to Maven Central are not allowed to be used for certification.

`mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-platform-tck`

## Test Execution

Execute maven test with verify from the top-level directory:

`mvn clean verify -Ppayara-server-managed,jakarta-staging,appclient -pl . -pl jsonb-platform-tck`