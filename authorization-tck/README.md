# Jakarta Authorization TCK Runner

## Prerequisite

Download and install the TCK into your local Maven repo. 
From the top-level directory: `mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-authorization-tck -Pjakarta-staging`

## Test Execution

To execute the full TCK against a managed Payara Server

```
mvn clean verify 
```

### Debugging

Utilise the `payara-server-remote` profile. 
This profile expects that the TCK has been run in its entirety at least once to perform the necessary setup.

* Move into the TCK for the rerun: `cd authorization-tck/target/authorization-tck-3.0.0/tck`
* Install the common TCK module (make sure you don't clean!): `mvn install -Ppayara-server-remote,jakarta-staging -pl . -pl common -DskipTests`
* Start the configured Payara Server found at `authorization-tck/target/authorization-tck-3.0.0/tck/target/payara7` and attach your debugger
* Run your specific TCK module test (make sure you don't clean!), for example: `mvn verify -Ppayara-server-remote,jakarta-staging -f app-custom-policy2 -Dit.test=AppCustomPolicy2IT#testAuthenticatedSpecial`
