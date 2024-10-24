# Jakarta Tags

## Prerequisites
Download and install the TCK from the tck-downloads module. From the top-level directory:

`mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-tags-tck -Dpayara.version=...`

## Test Executions
Run maven test from the module directory

```
cd tags-tck
mvn clean verify -Dpayara.version=...
```