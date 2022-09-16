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

Run `mvn verify -Dpayara.home=/path/to/payara` in your desired module.
There is no "run all" currently until the issue with port conflicts is resolved.
