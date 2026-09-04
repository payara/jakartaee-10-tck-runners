# Jakarta MVC TCK Runner

## Prerequisite

Download and install the TCK into your local Maven repo.
From the top-level directory:

```
mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-mvc-tck
```

## Test Execution

### Payara Server (managed)

Starts and stops a Payara Server instance automatically. Run from the top-level directory:

```
mvn clean verify -pl . -pl mvc-tck -Ppayara-server-managed
```

### Payara Server (remote)

Requires a Payara Server already running on `localhost:4848`. Run from the top-level directory:

```
mvn clean verify -pl . -pl mvc-tck -Ppayara-server-remote -Dpayara.version=<version> -Dpayara.home=<path-to-payara>
```

The TCK does not require any server-side configuration.

### Payara Micro

Run from the top-level directory:

```
mvn clean verify -pl . -pl mvc-tck -Ppayara.micro.managed -Dpayara.version=<version>
```

### Payara Micro Platform

Run from the top-level directory:

```
mvn clean verify -pl . -pl mvc-tck -Ppayara-micro-managed -Ppayara-micro-platform -Dpayara.version=<version>
```
