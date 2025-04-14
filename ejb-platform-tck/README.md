# Jakarta Enterprise Beans Platform TCK

## Prerequisite
Install the Platform TCK via tck-download module, since the ones published to Maven Central are not allowed to be used for certification.

`mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-platform-tck`

## Test Execution

Execute maven test with verify from the top-level directory:

`mvn clean verify -Ppayara-server-managed,jakarta-staging -pl . -pl ejb-platform-tck`

Alternatively, execute against a remote server:

`mvn clean verify -Ppayara-server-remote,jakarta-staging -pl . -pl ejb-platform-tck -Dpayara.home=/path/to/payara7`

### Execution Options

This runner will attempt to configure the server.

To skip starting and stopping the server, specify `-DskipServerStartStop=true`

To skip configuring the server, specify `-DskipConfig=true`

The TCK is made up of two parts, 'ejb30' and 'ejb32'. You can skip these if desired using 

To run a more specific subset of tests, look at using failsafe command-line overrides (`-Dit.test`) 
or editing the `<includes>` configuration of failsafe in the  pom.
