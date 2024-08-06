# Jakarta JSONB TCK

## Prerequisite
To be able to run the TCK test, you **must** currently use the `payara-server-remote` profile.

## Test Execution

Ensure you have installed the parent POM:
`mvn clean install -f ../pom.xml`

Execute the test suite, providing the path to the Payara installation for the signature test:
`mvn clean verify -Ppayara-server-remote -Dpayara.home=[path to payara installation]`

The test requires configuring a JVM option to pass (see https://github.com/jakartaee/jsonb-api/issues/330).
The runner will configure this for you and restart the DAS.
If you wish to skip this config & restart (e.g. for reruns), add the following option to the above command: `-Dskip.server.config`