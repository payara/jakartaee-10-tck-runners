# Jakarta WebSocket

## Prerequisites
Download and install the TCK from the tck-downloads module. From the top-level directory:

`mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-websocket-tck -Dpayara.version=...`

## Test Executions
Run maven test from the top-level directory using managed arquillian profile

```
mvn clean verify -Ppayara-server-managed -Dpayara.version=...  -pl . -pl pages-tck
```