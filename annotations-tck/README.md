# Jakarta Annotations

## Prerequisites
Download and install the TCK from the tck-downloads module. From the top-level directory:

`mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-annotations-tck -Dpayara.version=...`

## Test Executions

### Payara Server (remote)
**(Make sure the Payara server up and running)**

Run maven test from the module directory using remote arquillian profile, and provide the path to payara and its version

```
cd annotations-tck
mvn clean verify -Ppayara-server-remote -Dpayara.version=... -Dpayara.home=...
```

### Payara Micro
No running server is needed. The signature test classpath is built by extracting `jakarta.annotation-api.jar` directly from the Payara Micro fat jar, which Maven downloads automatically.

Run maven test from the top-level directory using the `-pl annotations-tck` option, and provide the Payara version:

```
mvn clean verify -pl annotations-tck -Ppayara-micro-managed -Dpayara.version=...
```