# Jakarta Faces TCK

## Prerequisite
Set `JAVA_HOME` to Java 21.

Use maven 3.9.6 or newer.

The runner updates sources of the test to depend on chrome version 131.*

Install the Faces TCK dependencies via tck-download module. Execute from the root directory of TCK runners:

```
mvn clean install -pl .,:tck-download,:jakarta-faces-tck
```

## Test Execution

Currently, *only remote testing* is supported.

Run Payara 7 with default setup (with ports 4848 and 8080).

Execute maven test with verify from the this directory:

```
mvn clean install
```

In case it's necessary to specify exact versions for testing purposes, the typical command looks like:
```
mvn -B clean install -Dtck.mode=platform -Dmojarra.noupdate=true -Dpayara.version=7.2025.1.Alpha1-SNAPSHOT -Dfaces.version=4.1.1 
```

To run only old test, add:
```
-Dmaven.exec.skipping="-pl :old-faces-tck-parent,:old-tck-build,:old-tck-run"
```

If you want to skip selenium tests (it depends on chrome), add:
```
-Dmaven.exec.skipping="-Dtest.selenium=false"
```