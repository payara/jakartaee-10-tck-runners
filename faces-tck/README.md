# Jakarta Faces TCK

## Prerequisite
Set `JAVA_HOME` to Java 21.

Install the Faces TCK dependencies via tck-download module. Execute from the root directory of TCK runners:

```
mvn clean install -pl .,:tck-download,:jakarta-faces-tck
```

## Test Execution

Currently, *only remote testing* is supported.

Run Payara 7 with default setup (with ports 4848 and 8080).

Execute maven test with verify from the this directory:

```
mvn clean verify
```
