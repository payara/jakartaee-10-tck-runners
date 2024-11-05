# Jakarta Annotations

## Prerequisites
Download and install the TCK from the tck-downloads module. From the top-level directory:

`mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-annotations-tck -Dpayara.version=...`

## Test Executions
**(Make sure the Payara server up and running)**

Run maven test from the module directory using remote arquillian profile, and provide the path to payara and its version

```
cd annotations-tck
mvn clean verify -Ppayara-server-remote -Dpayara.version=... -Dpayara.home=...
```