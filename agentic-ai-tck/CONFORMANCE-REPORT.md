# Jakarta Agentic AI TCK — Payara Server Conformance Report

**Date:** 2026-09-28
**Tickets:** FISH-14677 (run + gap analysis), FISH-14726 (implementation fixes)
**TCK under test:** `jakarta.agentic-ai:jakarta.agentic-ai-tck` `1.0.0-SNAPSHOT`
(branch `tck/gaps-in-spec-coverage`, PR #74 — the hardened TCK with the G1–G10 coverage additions)
**Implementation under test:** Payara Server 7 built from source with the FISH-14726 fix
(`agentic-ai-core.jar`, branch `FISH-14726-agentic-ai-lifecycle-validation-gaps`,
PR payara/Payara#8457) in `glassfish/modules`.
**Runtime:** JDK 21.0.12-zulu (Payara built and run with the same JDK).

## Runs

The hardened TCK was executed against the patched Payara build in **both** Arquillian
modes. Both runs use `jakarta.ai.agent.tck.implementation.present=true` on the server
JVM (baked into the domain via `asadmin create-jvm-options`) and on the client/test
JVM (Failsafe `systemPropertyVariables`).

| Mode | Profile | Command highlights |
|---|---|---|
| Remote | `payara-server-remote` | adapter attaches to an already-running domain (`skipConfig=true`) |
| Managed | `payara-server-managed` | adapter starts/stops the domain; `payara.home` pointed at the patched build |

### Aggregate result (identical in both modes)

| Metric | Count |
|---|---:|
| Tests run | 216 |
| Passed | 177 |
| Failed | 0 |
| Errors | 6 |
| Skipped | 33 |

Before the FISH-14726 fix the remote run scored **176 pass / 1 fail / 6 err** — the
single failure was `InheritedPhaseTests.inheritedActionParticipates`. The fix takes
that to **177 pass / 0 fail**.

- **Signature tests: 12/12 pass** — the Payara `jakarta.agentic-ai-api.jar` matches
  the specification API signature. The API surface is conformant.
- The 33 skips are all the `@RequiresNoImplementation` baseline preconditions,
  correctly superseded by their `@RequiresImplementation` counterparts because a
  compatible implementation is present. No skip is unexpected.
- **All 6 remaining errors are a harness artifact of the Payara Arquillian adapter,
  not implementation defects** (see below). Every one of the six deployments is
  rejected by the container for exactly the right reason.

## The 6 deployment-error "errors" are adapter-wrapping, in both modes

The six `DeploymentError*Tests` assert that an invalid agent is *rejected at deploy
time* (`@ShouldThrowException(jakarta.enterprise.inject.spi.DeploymentException)`;
`DefinitionException` is a subclass). Payara rejects all six correctly — but the
assertion cannot be scored green by **either** Payara adapter, because both the
`remote` and the `managed` adapters deploy through `asadmin` (out-of-process). The
container-side CDI exception is therefore marshalled across the process boundary as
**text**, not as a live exception object:

```
org.jboss.arquillian.container.spi.client.container.DeploymentException: Could not deploy deployerror-twophase.war
  Caused by: fish.payara.arquillian.container.payara.clientutils.PayaraClientException:
    ... CDI definition failure ...
    jakarta.enterprise.inject.spi.DefinitionException: @Agent ... TwoPhaseMethodAgent
      method doStuff cannot declare more than one phase annotation
```

`@ShouldThrowException` matches by exception *type* against the objects in the cause
chain. The real `jakarta...DefinitionException` never becomes an object in that
chain — it survives only as a string inside `PayaraClientException` — so the match
fails and Arquillian records an *error*. This is inherent to deploying via `asadmin`
and is **identical in remote and managed mode**. Only a truly in-process
(*embedded*) adapter would expose the CDI exception object for the match; Payara has
no embedded Arquillian adapter in this setup.

The managed run is nonetheless the stronger evidence: it captures the exact CDI
`DefinitionException` message directly in each test's own report cause chain (below),
so no separate `server.log` inspection is needed to confirm the rejection.

| Test / Assertion | Container-side rejection (from the managed report cause chain) |
|---|---|
| `DeploymentErrorNoTriggerTests` | `@Agent … NoTriggerAgent must declare @Trigger` |
| `DeploymentErrorTwoTriggerTests` | `@Agent … TwoTriggerAgent cannot have more than one @Trigger annotation` |
| `DeploymentErrorTwoOutcomeTests` | `@Agent … TwoOutcomeAgent cannot have more than one @Outcome annotation` |
| `DeploymentErrorMixedOrderingTests` | `Inconsistent order at @Agent … MixedOrderingAgent: all @Decision/@Action should declare @Priority or order or nothing` |
| `DeploymentErrorTwoPhaseTests` | `@Agent … TwoPhaseMethodAgent method doStuff cannot declare more than one phase annotation` |
| `DeploymentErrorMultiEventTriggerTests` | `@Agent … MultiEventTriggerAgent @Trigger method cannot declare more than one event parameter` |

**Verdict:** Payara enforces all six deploy-time rules correctly. The six errors are
a TCK/adapter portability artifact (the Payara Arquillian adapter always deploys via
`asadmin`), not a Payara conformance gap. To turn them green the TCK would need an
in-process/embedded adapter, or to relax `@ShouldThrowException` to also accept the
adapter's wrapped `DeploymentException` / inspect the cause message.

## Implementation gaps found and fixed (FISH-14726)

The initial run (hardened TCK against unmodified Payara) surfaced three genuine
gaps, all in `AgenticAIExtension.buildMetadata`. All three are fixed in
PR payara/Payara#8457 and confirmed by the re-run:

1. **Inherited `@Action` from a non-`@Agent` superclass was not invoked** —
   `InheritedPhaseTests.inheritedActionParticipates` observed `[TRIGGER]` instead of
   `[TRIGGER, ACTION]`. `buildMetadata` now collects phase methods across the class
   hierarchy (most-derived first, overrides shadowing) instead of only
   `getDeclaredMethods()`. **Now passes.**
2. **Two phase annotations on one method were not rejected** — a method annotated
   both `@Action` and `@Decision` deployed successfully (`AGENTICAI-DEPLOY-ERR-001`).
   A new `countPhaseAnnotations` check raises `DefinitionException`. **Now rejected**
   (`TwoPhaseMethodAgent … cannot declare more than one phase annotation`).
3. **A `@Trigger` with more than one event parameter was not rejected**
   (`AGENTICAI-DEPLOY-ERR-003C`). A new `countEventParameters` check raises
   `DefinitionException`. **Now rejected** (`MultiEventTriggerAgent … cannot declare
   more than one event parameter`).

## Harness fixes required to obtain a meaningful run

Prerequisites for anyone reproducing these runs:

1. **Client-classpath API dependencies.** The TCK receives `jakarta.jakartaee-api`
   in `provided` scope from its parent POM, so it is *not* transitive to this
   runner. The G1 validation tests reference `jakarta.validation`, which caused
   JUnit Platform discovery to abort with
   `NoClassDefFoundError: jakarta/validation/ConstraintViolationException`
   (aborting the whole suite). Added `jakarta.validation-api` (test scope) to the
   runner POM, mirroring the existing `cdi-api` and Yasson entries.

2. **Client-side implementation-present flag.** The deployment-error tests gate at
   the class level with `@EnabledIfSystemProperty(...implementation.present)`, which
   JUnit evaluates in the **client** JVM (the `@ShouldThrowException` deployment
   check runs before the in-container implementation probe). The flag was previously
   set only on the server, so all six deployment-error tests were silently skipped.
   Added `jakarta.ai.agent.tck.implementation.present=true` to the Failsafe
   `systemPropertyVariables`.

3. **TCK framework package (spec repo, PR #74).** The Arquillian
   `AgenticAIFrameworkProcessor` did not deploy the
   `ee.jakarta.tck.ai.agent.framework.workflow` package, so `WorkflowContext`
   (referenced by `LargeLanguageModelStub`) was missing at runtime and produced 15
   `NoClassDefFoundError` errors against a real implementation. Adding the package
   cleared all 15.

## Reproduction

Remote (against an already-running patched domain with the flag baked in):

```bash
mvn clean verify -Ppayara-server-remote \
  -Djakarta.tck.agentic-ai.version=1.0.0-SNAPSHOT \
  -Djakarta.agentic-ai.api.version=1.0.0-SNAPSHOT \
  -nsu -pl agentic-ai-tck
```

Managed (adapter starts/stops the patched build; `payara.home` points at it,
`skipConfig=true` so the stock distribution is not unpacked over it):

```bash
mvn clean verify -Ppayara-server-managed -DskipConfig=true \
  -Dpayara.home=/path/to/patched/payara7 \
  -Djakarta.tck.agentic-ai.version=1.0.0-SNAPSHOT \
  -Djakarta.agentic-ai.api.version=1.0.0-SNAPSHOT \
  -nsu -pl agentic-ai-tck
```

## Summary for the ticket

- **API signature: conformant** (12/12).
- **Behavioral & lifecycle coverage: 177 pass, 0 fail** in both remote and managed
  mode after the FISH-14726 fix.
- **3 real gaps fixed** (FISH-14726 / PR #8457): inherited-superclass `@Action`
  invoked; two-phase-annotations-per-method rejected; multi-parameter `@Trigger`
  rejected.
- **6 deployment-error rules are all enforced correctly** but are unverifiable by
  `@ShouldThrowException` type-matching under either Payara adapter, because both
  deploy via `asadmin` and marshal the CDI exception as text. The managed report's
  cause chain contains the exact `DefinitionException` for each. This is a TCK/adapter
  portability note, not a Payara defect.

**Net: Payara Server passes the Jakarta Agentic AI TCK** — 0 failures, and all six
adapter-masked errors correspond to deployments the container correctly rejects.
