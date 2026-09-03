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

```
cd annotations-tck
mvn clean verify -Ppayara-micro-managed -Dpayara.version=...
```
