# Jakarta REST TCK

Copied to a separate module until we refactor the remote tests to managed profile or configure a way to avoid port conflicts.

## Prerequisities

* Install TCK artifact in local Maven repository by running `mvn install` in `/tck-download/jakarta-rest-tck`.
* Make sure you don't have a running server

### Against a full Payara Server (default)

* Run `mvn verify`

The implementation jars are read directly from `${payara.home}/glassfish/modules`, so a
Payara Server installation is required (see `payara.home` below).

### Against Payara Micro

* Run `mvn verify -Ppayara-micro-managed`

The implementation jars are extracted from the `fish.payara.extras:payara-micro` distribution
artifact (its `MICRO-INF/runtime/*.jar`) into a synthesized `target/micro-home/glassfish/modules`
directory, and `payara.home` is overridden to `target/micro-home` so the same classpath resolves
to the Micro jars, so **no full server installation is required**.

## Additional properties

`payara.home`:
  Path to a full Payara Server installation, used by the default profile to locate the
  implementation jars under `${payara.home}/glassfish/modules`. Defaults to
  `<repo root>/target/payara7`.