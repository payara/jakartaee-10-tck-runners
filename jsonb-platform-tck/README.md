# Jakarta JSONB Extra Platform TCK

## Prerequisite
Download and install the TCK from the tck-downloads module. From the top-level directory:

`mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-jsonb-platform-tck -Dpayara.version=...`

## Test Execution

Run maven test from the module directory using remote arquillian profile, and provide the path to payara and its version

```
cd jsonb-platform-tck
mvn clean verify -Ppayara-server-remote -Dpayara.version=... -Dpayara.home=...
```

The test requires configuring a JVM option to pass (see https://github.com/jakartaee/jsonb-api/issues/330).
The runner will configure this for you and restart the DAS.

To skip server startup and shutdown, set `-DskipServerStartStop` to true. Defaults to false.
To skip server configuration, set to `-DskipConfig` to true. Defaults to false.