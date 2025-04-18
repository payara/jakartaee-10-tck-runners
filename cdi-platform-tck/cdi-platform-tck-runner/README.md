# Jakarta CDI Platform TCK Runner

## Prerequisite
- Set `JAVA_HOME` to Java 21.
- Ensure Docker is installed and properly configured.

## Download TCK Dependencies

Use the `tck-download` module to fetch the `jakarta-cdi-tck` and `jakarta-platform-tck` artifacts.

Run the following command from the root directory of the TCK runners:

```
mvn clean install -pl .,:tck-download,:jakarta-cdi-tck,:jakarta-platform-tck
```

## Test Execution

`mvn clean verify -pl . -pl cdi-platform-tck/ -pl cdi-platform-tck/cdi-platform-tck-runner -Dpayara.version=7.2025.1.Alpha1-SNAPSHOT -Ppayara-server-managed,jakarta-staging`