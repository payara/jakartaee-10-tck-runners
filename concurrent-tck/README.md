# Concurrent 3.0

## Running

Run from the top-level directory:

`mvn clean verify -Ppayara-server-managed -Dpayara.version=... -pl . -pl tck-download -pl tck-download/jakarta-concurrency-tck -pl concurrent-tck`

For running against Payara Web Profile, activate the `web` profile

If intending to run against a remote server (`-Ppayara-server-remote`), then you must provide the `-Dpayara.home` property.

To skip server startup and shutdown, set `-DskipServerStartStop` to true. Defaults to true for remote profile, false otherwise.
To skip server configuration, set to `-DskipConfig` to true. Defaults to false.