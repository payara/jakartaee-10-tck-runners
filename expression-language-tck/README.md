# Jakarta Expression Language

## Prerequisites
Download and install the TCK from the tck-downloads module. From the top-level directory:

`mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-expression-language-tck -Dpayara.version=...`

## Test Executions
**(Make sure the Payara server up and running)**

Run maven test from the top-level directory using remote arquillian profile, and provide the path to payara and its version

```
mvn clean verify -Ppayara-server-remote -Dpayara.version=... -Dpayara.home=... -pl . -pl expression-language-tck
```

To run against a managed Payara Micro instead:

```
mvn clean verify -Ppayara-micro-managed -Dpayara.version=... -pl . -pl expression-language-tck
```