# Jakarta Core Profile TCK

## Prerequisite
Install the Core Profile TCK dependencies via tck-download module, since the ones published to Maven Central are not allowed to be used for certification.

`mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-core-profile-tck`

## Test Execution

Execute maven test with verify from the top-level directory:

`mvn clean verify -Dpayara.home=[path to payara installation] -Ppayara-server-managed -pl . -pl core-tck`