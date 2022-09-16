# Jakarta REST TCK

Comprised of two modules:

* payara-server-remote-tests - runs the TCK against a running Payara Server instance
* se-tests - runs the SE tests, requires Payara Server to not be running

## Prerequisities

* Install TCK artifact in local Maven repository by running `mvn install` in `/tck-download/jakarta-rest-tck`.

## Additional properties

`payara.home`:
path to payara installation, so that SigTest can verify jar files statically

## Running

### Main TCK
Run `mvn verify -Dpayara.home=/path/to/payara`

### SE Tests
* Make sure you don't have a running server
* `cd se-tests`
* Run `mvn verify -Dpayara.home=/path/to/payara`

