# Jakarta REST TCK

Comprised of two modules:

* payara-server-remote-tests - runs the TCK against a running Payara Server instance
* se-tests - runs the SE tests, requires Payara Server to not be running

## Prerequisities

Download and install the TCK from the tck-downloads module. From the top-level directory:

`mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-rest-tck -Dpayara.version=...`

## Running

### Main TCK
**(Make sure the Payara server up and running)**

Run maven test from the module directory using remote arquillian profile, and provide the path to payara and its version

```
cd rest-tck
mvn clean verify -Dpayara.version=... -Dpayara.home=...
```

### SE Tests
* Make sure you don't have a running server
* `cd se-tests`
* Run `mvn verify -Dpayara.home=/path/to/payara`

