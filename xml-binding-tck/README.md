# Jakarta XML Binding TCK

Runs the [Jakarta XML Binding 4.0 TCK](https://jakarta.ee/specifications/xml-binding/4.0/) against Payara Server.

> **Unix/Linux/macOS only.** The test harness relies on shell scripts bundled in the TCK ZIP and cannot run on Windows.

## How it works

Unlike most TCK runners in this repository (which use Arquillian + JUnit 5), the XML Binding TCK uses the **legacy JavaTest / JCK harness** bundled inside the TCK ZIP. The Maven build:

1. Downloads and unpacks `jakarta-xml-binding-tck-<version>.zip` into `target/`.
2. Downloads `checker.jar` (CheckerFramework) into `target/` — required by the TCK signature tests.
3. Invokes the runner, which configures and drives `javatest.jar` directly.
4. At the `verify` phase, Ant checks that `<report-dir>/JAXB-TCK/text/summary.txt` contains no `Failed.` entries.

By default (no profile) the Maven build calls the legacy `run-tck.sh` shell script (multi-jvm mode) — unchanged behaviour. The [fast execution modes](#fast-execution-modes) (`-Psingle-agent` / `-Pagent-pool`) instead call `RunTck.java`, a single-file Java program. We are gradually migrating off the shell script.

## Prerequisites

- JDK 21+ with `JAVA_HOME` set
- Payara Server 7 unpacked (path provided via `payara.home` or positional argument)
- The TCK artifact installed into the local Maven repository (see below)

## Step 1 — Install the TCK artifact

The TCK ZIP is not on Maven Central. Download it from the Eclipse download site and install it locally. From the **top-level** directory:

```
mvn clean install \
  -pl . -pl tck-download -pl tck-download/jakarta-xml-binding-tck \
  -Dpayara.version=<version>
```

This downloads `jakarta-xml-binding-tck-4.0.2.zip` from `https://download.eclipse.org/jakartaee/xml-binding/4.0/` and installs it as `jakarta.tck:jakarta-xml-binding-tck:4.0.2:zip` in the local Maven repository.

## Step 2 — Run the TCK

From the **top-level** directory:

```
mvn clean verify \
  -Ppayara-server-managed \
  -Dpayara.version=<version> \
  -pl . -pl xml-binding-tck
```

To run with multiple concurrent threads (speeds up execution):

```
mvn clean verify \
  -Ppayara-server-managed \
  -Dpayara.version=<version> \
  -Dconcurrent.threads=4 \
  -pl . -pl xml-binding-tck
```

> `payara-server-managed` unpacks the Payara ZIP into `target/payara7` and passes that path to the runner. The XML Binding TCK does **not** deploy anything to Payara — the server is used only as a source of JAXB implementation JARs.

## Fast execution modes

The full TCK is dominated by ~28,000 XML Schema test groups. In the default `multi-jvm` mode every schema test group spawns **two** short-lived JVMs (one for `xjc`, one for the test executor) — on the order of ~57,000 JVM starts, which makes a full run very slow. Two faster modes run the tests inside long-lived JavaTest **agent** JVMs, where XJC/schemagen run in-process and the harness schema cache actually works.

| Mode (profile) | Runner | What it does | Isolation | When to use |
|---|---|---|---|---|
| `multi-jvm` (default, no profile) | `run-tck.sh` | Fresh JVM per compile + per test, via bundled shell scripts | Per test (process) | Certification / reference behaviour |
| `single-agent` (`-Psingle-agent`) | `RunTck.java` | One long-lived agent JVM, N concurrent tests in one heap | Shared heap | Fastest for dev/CI on a trusted machine |
| `agent-pool` (`-Pagent-pool`) | `RunTck.java` | N long-lived agent JVMs, each single-threaded | Per agent (process) | Fast **and** isolated; safest fast mode |

Both fast modes auto-detect concurrency from `Runtime.getRuntime().availableProcessors()`. Override with `-Dconcurrent.threads=N`.

```bash
# Single long-lived agent JVM, auto-detected concurrency
mvn clean verify -Ppayara-server-managed,single-agent \
  -Dpayara.version=<version> -pl . -pl xml-binding-tck

# Pool of agent JVMs (one test each), explicit pool size
mvn clean verify -Ppayara-server-managed,agent-pool \
  -Dconcurrent.threads=8 -Dpayara.version=<version> -pl . -pl xml-binding-tck
```

Results are written to a per-mode report directory so runs can coexist:

| Mode | Report dir (under `target/`) | Work dir | Archive |
|---|---|---|---|
| `multi-jvm` | `JAXB_REPORT/` | `batch-multiJVM/` | `jaxbtck-multi-jvm-results.tar.gz` |
| `single-agent` | `SINGLE_AGENT_REPORT/` | `batch-singleJVM/` | `jaxbtck-single-agent-results.tar.gz` |
| `agent-pool` | `AGENT_POOL_REPORT/` | `batch-agentPool/` | `jaxbtck-agent-pool-results.tar.gz` |

> **Caveat (shared heap).** In `single-agent` mode all tests share one JVM. A few TCK statics — notably a shared `SchemaFactory` and `System.out`/`System.err` redirection during XJC — are not thread-safe, so very high concurrency in a single agent can produce flaky, non-reproducible schema failures. If you see those, prefer `agent-pool` (each JVM is single-threaded) or lower `-Dconcurrent.threads`. The JAXB RI's XJC/schemagen also require a **full JDK** (not a JRE) because schemagen runs the annotation processor via the system Java compiler.

### Running `RunTck.java` directly

The runner needs no compilation (JDK 11+ single-file source launch):

```bash
java RunTck.java <mode> <payara-home> <concurrency> <report-dir>
# e.g.
java RunTck.java single-agent ../target/payara7 0 target/SINGLE_AGENT_REPORT
```

| Argument | Description |
|---|---|
| `mode` | `multi-jvm`, `single-agent`, or `agent-pool` |
| `payara-home` | Path to the Payara installation (contains `glassfish/`) |
| `concurrency` | Number of concurrent tests; `0` = auto-detect |
| `report-dir` | Output directory for the JavaTest report |

CDS and class-unloading JVM flags for the agent JVMs are present but commented out near `startAgents` in `RunTck.java` — enable them to shave further start-up/metaspace cost on Java 21.

## Legacy shell script (`run-tck.sh`)

> This is the **default** runner when no profile is selected (multi-jvm mode). It is
> functionally equivalent to `RunTck.java multi-jvm`; the fast-mode profiles use
> `RunTck.java` instead, and we are gradually migrating the default off this script.

`run-tck.sh` accepts 0, 1, or 2 positional arguments:

```
bash run-tck.sh [payara-home] [concurrent-threads]
```

| Argument | Default | Description |
|---|---|---|
| `payara-home` | `../target/payara7` (relative to script) | Path to the Payara installation |
| `concurrent-threads` | `1` | Number of concurrent JavaTest threads |

## What `run-tck.sh` does

The script performs the following steps:

### 1. Patch configuration files

Two files inside the unpacked TCK are patched in-place using `sed`:

**`target/xml-binding-tck/testsuite.jtt`**
- Sets `finder` to point to the binary test index (`testsuite.jtd`) in the unpacked location.

**`target/xml-binding-tck/lib/javasoft-multiJVM.jti`** (JavaTest Interview / configuration file)

| Setting | Value |
|---|---|
| `jck.env.jaxb.classes.jaxbClasses` | Payara JAXB JARs + `checker.jar` (see below) |
| `jck.env.jaxb.testExecute.cmdAsFile` | `$JAVA_HOME/bin/java` |
| `WORKDIR` | `target/xml-binding-tck/batch-multiJVM/work/` |
| `TESTSUITE` | `target/xml-binding-tck/` |
| `jck.env.jaxb.testExecute.otherEnvVars` | `JAVA_HOME` and `JAXB_HOME` (set to `$PAYARA_HOME/glassfish`) |
| `jck.env.jaxb.schemagen.run.jxcCmd` | `/bin/sh linux/bin/schemagen.sh` |
| `jck.concurrency.concurrency` | `concurrent-threads` argument (if provided) |

### 2. JAXB implementation JARs

The following JARs from the Payara installation are placed on the TCK classpath:

| JAR | Purpose |
|---|---|
| `glassfish/modules/jakarta.xml.bind-api.jar` | Jakarta XML Binding API |
| `glassfish/modules/jaxb-osgi.jar` | JAXB RI (Reference Implementation) |
| `glassfish/modules/jersey-media-jaxb.jar` | Jersey JAXB media support |
| `glassfish/modules/jakarta.activation-api.jar` | Jakarta Activation API (required by JAXB) |
| `target/checker.jar` | CheckerFramework (required by signature tests) |

### 3. Run JavaTest in two passes

**Pass 1 — full run:**
```
java -jar javatest.jar -batch -testsuite ... -open javasoft-multiJVM.jti \
  -workdir -create batch-multiJVM/work \
  -set jck.env.jaxb.xsd_compiler.skipValidationOptional Yes \
  -set jck.env.jaxb.xsd_compiler.testCompile.xjcCmd "/bin/sh linux/bin/xjc.sh" \
  ... -runtests
```

**Pass 2 — rerun only tests that did not run:**
Same command, but adds `-set jck.priorStatus.status not_run` to pick up any tests skipped in pass 1.

**Report generation:**
```
java -jar javatest.jar -workdir batch-multiJVM/work -writereport JAXB_REPORT/JAXB-TCK
```

### 4. Convert results to JUnit XML

`JTReportParser.jar` (bundled in this module's root) converts the JavaTest report into JUnit XML format compatible with CI systems:

```
java -jar JTReportParser.jar args.txt JAXB_REPORT results/junitreports/
```

### 5. Archive results

All output is packed into `target/jaxbtck-results.tar.gz`:
- `JAXB_REPORT/` — JavaTest HTML and text reports
- `batch-multiJVM/work/` — JavaTest work directory (per-test results)
- `results/junitreports/` — JUnit XML reports

## Verification

After the script finishes, `maven-antrun-plugin` (in the `verify` phase) checks:

```
target/JAXB_REPORT/JAXB-TCK/text/summary.txt
```

If the file contains the string `Failed.`, the build fails. This is the primary pass/fail gate for the Maven build.

## Output files

| Path | Description |
|---|---|
| `target/JAXB_REPORT/JAXB-TCK/text/summary.txt` | Summary: passed/failed/error counts |
| `target/JAXB_REPORT/JAXB-TCK/` | Full JavaTest HTML and text reports |
| `target/results/junitreports/` | JUnit XML (for CI import) |
| `target/batch-multiJVM/work/` | JavaTest work directory |
| `target/jaxbtck-results.tar.gz` | Archive of all above |
