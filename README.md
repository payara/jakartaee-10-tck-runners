# jakartaee-10-tck-runners

Payara runners for the standalone Jakarta EE TCKs. Each module integrates a Jakarta EE specification TCK with Payara Server so that compliance can be verified via Maven.

## Prerequisites

- **JDK 21** or later
- **Maven 3.9+**
- **Payara Server 7** (either downloaded automatically via the `payara-server-managed` profile, or already running for `payara-server-remote`)
- **Unix/Linux/macOS** — the `xml-binding-tck` module does not support Windows

## Repository Structure

```
jakartaee-10-tck-runners/
├── tck-download/          # Downloads & installs TCK ZIPs into local Maven repo
│   └── jakarta-*/         # One sub-module per TCK artifact
├── <spec>-tck/            # Runner modules, one per Jakarta EE spec
├── summarizer/            # Utility: aggregates TCK results into a single report
└── pom.xml                # Root POM: dependency versions, Payara profiles
```

### `tck-download`

Many TCK artifacts are not published to Maven Central and must be downloaded from the Eclipse download site and installed into the local Maven repository before the runner modules can use them. Each sub-module under `tck-download` handles one TCK.

```
mvn clean install -pl . -pl tck-download -pl tck-download/<spec>-tck -Dpayara.version=<version>
```

### Runner modules

Each `<spec>-tck` module contains the Maven configuration to run the tests against a Payara Server instance. Two testing approaches are used across the modules:

| Approach | Modules | Notes |
|---|---|---|
| **Arquillian + JUnit 5** (`maven-failsafe-plugin`) | Most TCKs (jsonb, jsonp, rest, servlet, cdi-platform, persistence, concurrent, …) | Tests run inside or against a live Payara instance via Arquillian. Supports `payara-server-managed` (Maven starts/stops Payara) and `payara-server-remote` (Payara already running). |
| **JavaTest / JCK harness** (`run-tck.sh` shell script) | `xml-binding-tck` | Uses the legacy JavaTest harness bundled with the TCK ZIP. Maven unpacks the TCK, then invokes the shell script which drives `javatest.jar` directly. |

### `summarizer`

A standalone Java utility that reads test output from multiple formats (JUnit XML, `summary.txt`, `testSet`, failsafe summaries, or collections) and produces a single consolidated report. See `summarizer/README.md`.

## General Usage

### Managed mode (Maven controls the Payara lifecycle)

```
mvn clean verify \
  -Ppayara-server-managed \
  -Dpayara.version=<payara-version> \
  -pl . -pl tck-download -pl tck-download/<spec>-tck -pl <spec>-tck
```

### Remote mode (Payara is already running)

```
mvn clean verify \
  -Ppayara-server-remote \
  -Dpayara.version=<payara-version> \
  -Dpayara.home=<path-to-payara> \
  -pl . -pl <spec>-tck
```

> **Note:** Remote mode skips the `tck-download` step, so the TCK artifact must already be in the local Maven repository.

## Module Overview

| Module | Jakarta EE Spec | TCK Version | Test Harness |
|---|---|---|---|
| `activation-tck` | Jakarta Activation | — | Arquillian/JUnit 5 |
| `annotations-tck` | Jakarta Annotations | 3.0.0 | Arquillian/JUnit 5 |
| `authentication-tck` | Jakarta Authentication | 3.1.0 | Arquillian/JUnit 5 |
| `authorization-tck` | Jakarta Authorization | 3.0.0 | Arquillian/JUnit 5 |
| `batch-tck` | Jakarta Batch | 2.1.5 | Arquillian/JUnit 5 |
| `bean-validation-tck` | Jakarta Bean Validation | 3.1.1 | Arquillian/JUnit 5 |
| `cdi-langmodel-tck` | CDI Language Model | — | Arquillian/JUnit 5 |
| `cdi-platform-tck` | CDI (Platform) | 4.1.0 | Ant + Arquillian |
| `cdi-tck` | CDI (Standalone) | 4.1.0 | Arquillian/JUnit 5 |
| `concurrent-tck` | Jakarta Concurrency | 3.1.1 | Arquillian/JUnit 5 |
| `core-tck` | Jakarta Core Profile | 11.0.0 | Arquillian/JUnit 5 |
| `data-tck` | Jakarta Data | 1.0.1 | Arquillian/JUnit 5 |
| `expression-language-tck` | Jakarta Expression Language | 6.0.0 | Arquillian/JUnit 5 |
| `faces-tck` | Jakarta Faces | 4.1.1 | Arquillian/JUnit 5 |
| `inject-tck` | Jakarta Inject | 2.0.2 | Arquillian/JUnit 5 |
| `jsonb-tck` | Jakarta JSON Binding | 3.0.0 | Arquillian/JUnit 5 |
| `jsonp-tck` | Jakarta JSON Processing | 2.1.1 | Arquillian/JUnit 5 |
| `messaging-tck` | Jakarta Messaging | 3.1.0 | Arquillian/JUnit 5 |
| `mvc-tck` | Jakarta MVC | 3.0.0 | Arquillian/JUnit 5 |
| `pages-tck` | Jakarta Pages | 4.0.0 | Arquillian/JUnit 5 |
| `persistence-tck` | Jakarta Persistence | 3.2.0 | Arquillian/JUnit 5 |
| `rest-tck` | Jakarta REST | 4.0.0 | Arquillian/JUnit 5 |
| `security-tck` | Jakarta Security | — | Arquillian/JUnit 5 |
| `servlet-tck` | Jakarta Servlet | 6.1.0 | Arquillian/JUnit 5 |
| `soap-tck` | Jakarta SOAP | — | Arquillian/JUnit 5 |
| `tags-tck` | Jakarta Tags | — | Arquillian/JUnit 5 |
| `transaction-platform-tck` | Jakarta Transactions | 2.0.1 | Arquillian/JUnit 5 |
| `websocket-tck` | Jakarta WebSocket | 2.2.0 | Arquillian/JUnit 5 |
| `xml-binding-tck` | Jakarta XML Binding | 4.0.2 | JavaTest/JCK harness (shell script) |
| `xml-ws-tck` | Jakarta XML Web Services | — | Arquillian/JUnit 5 |

Platform TCK variants (`*-platform-tck`) run the same specs inside the full Jakarta EE Platform profile.

## Key Maven Properties

| Property | Default | Description |
|---|---|---|
| `payara.version` | `7.2025.1.Alpha4-SNAPSHOT` | Payara Server version to download/test against |
| `payara.home` | `target/payara7` | Path to unpacked Payara installation |
| `payara.artifact` | `payara` | Use `payara-web` for the Web Profile |
| `jakarta.tck.platform` | `full` | Platform level: `full`, `web`, or `core` |
| `skipTests` | `false` | Skip all test executions |
| `skipConfig` | `${skipTests}` | Skip server configuration steps |
| `skipServerStartStop` | `${skipTests}` | Skip starting/stopping the managed server |

## Profiles

| Profile | When to use |
|---|---|
| `payara-server-managed` | Maven downloads and manages the Payara lifecycle |
| `payara-server-remote` | Payara is already running; tests deploy and run against it |
| `web` | Runs the Web Profile variant of specs that support it |
| `windows` | Activated automatically on Windows (adjusts path separators and script extensions) |
| `payara-nexus-staging` | Enables Payara staging repository for pre-release artifacts |
