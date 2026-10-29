# Jakarta Annotations TCK Runner

This is the configuration to execute Annotations TCK.

## Prerequisite

Required property:

`payara.home` : path to payara installation

## Test Execution

### Payara Server

execute maven test:

```
mvn verify -Dpayara.home=pathtoyourpayara6server
```

### Payara Micro
No running server is needed. The signature test classpath is built by extracting `jakarta.annotation-api.jar` directly from the Payara Micro fat jar, which Maven downloads automatically.

Run maven test from the top-level directory using the `-pl annotations-tck` option, and provide the Payara version:

```
mvn clean verify -pl annotations-tck -Ppayara-micro-managed -Dpayara.version=...
```
