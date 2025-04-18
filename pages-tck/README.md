# Jakarta Pages

## Prerequisites
Download and install the TCK from the tck-downloads module. From the top-level directory:

`mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-pages-tck -Dpayara.version=7.2025.1.Alpha1-SNAPSHOT...`

## Test Executions
Run maven test from the top-level directory using managed arquillian profile

```
mvn clean verify -Ppayara-server-managed -Dpayara.version=7.2025.1.Alpha1-SNAPSHOT -pl . -pl pages-tck
```