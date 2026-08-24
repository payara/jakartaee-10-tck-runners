# Jakarta REST TCK

Copied to a separate module until we refactor the remote tests to managed profile or configure a way to avoid port conflicts.

## Prerequisities

* Install TCK artifact in local Maven repository by running `mvn install` in `/tck-download/jakarta-rest-tck`.
* Make sure you don't have a running server
* Run `mvn verify` here.

All test dependencies (Jersey, Jakarta APIs, parsson, yasson, etc.) are resolved from Maven
via the `payara-bom`, so no local Payara installation or `payara.home` property is required.

