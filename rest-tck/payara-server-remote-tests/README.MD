# Jakarta REST TCK

## Prerequisities

* Install TCK artifact in local Maven repository by running `mvn install` in `/tck-download/jakarta-rest-tck`.
* Have payara server running locally at standard ports (4848, 8080).
* Run `mvn verify` here.

## Additional properties

`payara.home`:
  path to payara installation, so that SigTest can verify jar files statically

