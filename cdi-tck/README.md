# Jakarta CDI TCK

## Prerequisite
Download and install the TCK from the tck-downloads module. From the top-level directory:

`mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-cdi-tck -Dpayara.version=...`

Ensure the `M2_HOME`, `MAVEN_HOME`, and `ANT_HOME` environment variables are set.

Initialise the porting submodules:

`git submodule update --init`

Build the glassfish porting:

`mvn clean install -f cdi-tck/glassfish-cdi-porting-tck`

## Test Execution

Run maven test from the module directory using managed arquillian profile

```
mvn clean verify -Ppayara-server-managed -Dpayara.version=... -pl . -pl cdi-tck
```

To run only the Core Profile parts, add `-Djavaee.level=core` to the above command.
