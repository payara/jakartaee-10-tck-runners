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

Requires a Payara Server already running on `localhost:4848`. Can be run directly from the `mvc-tck` directory:

```
cd mvc-tck
mvn clean verify -Ppayara-server-remote -Dpayara.version=<version> -Dpayara.home=<path-to-payara>
```

The TCK does not require any server-side configuration.

### Payara Micro

Before running, install the Payara Micro JAR into your local Maven repository from the Payara source tree:

```
mvn install -f <payara-enterprise-source>/appserver/extras/payara-micro/payara-micro-distribution/pom.xml -DskipTests
```

Then run the TCK (from the top-level directory or the `mvc-tck` directory):

```
# From top-level
mvn clean verify -pl . -pl mvc-tck -Dpayara.micro.managed -Dpayara.version=<version>

# From mvc-tck directory
cd mvc-tck
mvn clean verify -Dpayara.micro.managed -Dpayara.version=<version>
```

### Payara Micro Platform

Before running, install the Payara Micro Platform JAR into your local Maven repository:

```
mvn install -f <payara-enterprise-source>/appserver/extras/payara-micro/payara-micro-platform-distribution/pom.xml -DskipTests
```

Then run the TCK:

```
# From top-level
mvn clean verify -pl . -pl mvc-tck -Dpayara.micro.platform.managed -Dpayara.version=<version>

# From mvc-tck directory
cd mvc-tck
mvn clean verify -Dpayara.micro.platform.managed -Dpayara.version=<version>
```

> **Note:** Both Payara Micro profiles download the distribution JAR from the local Maven repository and launch it automatically — no running server is required.