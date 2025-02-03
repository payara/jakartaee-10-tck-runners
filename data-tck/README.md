# Jakarta Data

## Prerequisites
Download and install the TCK from the tck-downloads module. From the top-level directory:

`mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-data-tck -Dpayara.version=...`

## Test Executions
### Managed Profile
Run maven test from the top-level directory using managed arquillian profile

```
mvn clean verify -Ppayara-server-managed -Dpayara.version=...  -pl . -pl data-tck
```

### Remote Profile

Add the following required JVM option to the remote server and restart the server to apply the change:

```
asadmin create-jvm-options --add-opens=java.base/jdk.internal.vm.annotation=ALL-UNNAMED
asadmin restart-domain
```

Run maven test from the top-level directory using remote arquillian profile, providing the path to Payara.

```
mvn clean verify -Ppayara-server-remote -Dpayara.version=... -Dpayara.home=... -pl . -pl data-tck
```
