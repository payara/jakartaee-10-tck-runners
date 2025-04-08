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

If running on Windows and allowing the runner to configure the server for you, 
the JDBC resource that the runner will configure will be incorrect due to the parsing 
of ':' and '\' as parameter separators and escape characters instead of as valid Windows path characters.
To get around this, you can override the default maven configuration like so (substituting the path for your local environment): 
`"-DdatabaseName=D\:\\Git\\JakartaEE10-TCK-Runners\\target\\payara7\\glassfish\\domains\\domain1\\config\\derbyDB"`
