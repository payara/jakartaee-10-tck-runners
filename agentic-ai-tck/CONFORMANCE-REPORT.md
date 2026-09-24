# Jakarta Agentic AI TCK — Payara Server Conformance Report

**Date:** 2026-09-24
**Ticket:** FISH-14677
**TCK under test:** `jakarta.agentic-ai:jakarta.agentic-ai-tck` `1.0.0-SNAPSHOT`
(branch `tck/gaps-in-spec-coverage`, PR #74 — the hardened TCK with the G1–G10 coverage additions)
**Implementation under test:** Payara Server 7 (local build from source, `agentic-ai-core.jar` in `glassfish/modules`)
**Runtime:** JDK 21.0.12-zulu (Payara built and run with the same JDK)
**Mode:** Arquillian `payara-server-remote` against a running domain, with
`jakarta.ai.agent.tck.implementation.present=true` set on **both** the server JVM
(via `asadmin create-jvm-options`) and the client/test JVM (Failsafe
`systemPropertyVariables`).

## Aggregate result

| Metric | Count |
|---|---:|
| Tests run | 216 |
| Passed | 176 |
| Failed | 1 |
| Errors | 6 |
| Skipped | 33 |

- **Signature tests: 12/12 pass** — the Payara `jakarta.agentic-ai-api.jar` matches
  the specification API signature. The API surface is conformant.
- The 33 skips are all the `@RequiresNoImplementation` baseline preconditions,
  correctly superseded by their `@RequiresImplementation` counterparts because a
  compatible implementation is present. No skip is unexpected.
- Of the 7 non-passing results, **3 are genuine implementation gaps** and **4 are
  false negatives** caused by the Arquillian *remote* adapter masking the
  container's CDI exception type (see below). The implementation actually behaves
  correctly in those 4 cases.

## Genuine conformance gaps (3)

### 1. Inherited `@Action` from a non-`@Agent` superclass is not invoked
- **Test:** `InheritedPhaseTests.inheritedActionParticipates`
- **Observed:** execution trace `[TRIGGER]`
- **Expected:** `[TRIGGER, ACTION]`
- **Detail:** An `@Agent` subclass inherits an `@Action`-annotated method from a
  plain (non-`@Agent`) superclass. Payara runs the trigger but does not dispatch
  the inherited `@Action` phase. The workflow lifecycle scan does not walk the
  superclass hierarchy for phase methods.
- **Spec:** agent-lifecycle, "Inherited Methods".

### 2. Two phase annotations on one method are not rejected at deploy time
- **Test:** `DeploymentErrorTwoPhaseTests` (`AGENTICAI-DEPLOY-ERR-001`)
- **Fixture:** a method annotated with both `@Action` and `@Decision`.
- **Observed:** `deployerror-twophase` **deployed successfully**.
- **Expected:** deployment rejected with a `DefinitionException` /
  `DeploymentException`.
- **Detail:** Payara does not enforce the "one phase annotation per method" rule;
  the invalid agent deploys and starts.
- **Spec:** agent-lifecycle, "One Phase per Method".

### 3. A `@Trigger` with more than one event parameter is not rejected
- **Test:** `DeploymentErrorMultiEventTriggerTests` (`AGENTICAI-DEPLOY-ERR-003C`)
- **Fixture:** `@Trigger onEvent(@Observes DeploymentErrorEvent first, SecondTriggerEvent second)`.
- **Observed:** `deployerror-multievent` **deployed successfully**.
- **Expected:** deployment rejected.
- **Detail:** Payara does not validate the trigger-method parameter count; the
  agent with an extra trigger parameter deploys.
- **Spec:** agent-lifecycle (trigger parameter resolution).

## False negatives — implementation is conformant (4)

These four tests report an Arquillian *error*, but `server.log` shows Payara
**correctly rejected each deployment** with the expected
`jakarta.enterprise.inject.spi.DefinitionException` (a subclass of
`jakarta.enterprise.inject.spi.DeploymentException`). The failure is a harness
artifact: the Arquillian **remote** adapter cannot marshal the container-side CDI
exception across the wire, so it wraps every failure in its own
`org.jboss.arquillian.container.spi.client.container.DeploymentException`. That
Arquillian type is unrelated to the CDI `DeploymentException` named in
`@ShouldThrowException`, so the match fails even though the deployment failed for
exactly the right reason.

| Test / Assertion | Server-side rejection (from `server.log`) |
|---|---|
| `DeploymentErrorNoTriggerTests` (`DEPLOY-ERR-002`) | `@Agent … NoTriggerAgent must declare @Trigger` |
| `DeploymentErrorTwoTriggerTests` (`DEPLOY-ERR-...`) | `@Agent … TwoTriggerAgent cannot have more than one @Trigger annotation` |
| `DeploymentErrorTwoOutcomeTests` (`DEPLOY-ERR-...`) | `@Agent … TwoOutcomeAgent cannot have more than one @Outcome annotation` |
| `DeploymentErrorMixedOrderingTests` (`DEPLOY-ERR-...`) | `Inconsistent order at @Agent … MixedOrderingAgent: all @Decision/@Action should declare @Priority or order or nothing` |

**Verdict:** Payara enforces all four of these rules correctly. To have the TCK
score them as passes, these deployment-error cases must run under a **managed or
embedded** Arquillian adapter, where the actual CDI exception object is available
in-process for `@ShouldThrowException` to match. Under the remote adapter the
assertion cannot be evaluated by exception type; it must be confirmed from the
server log (as done here). This is a TCK portability note, not a Payara defect.

## Harness fixes required to obtain a meaningful run

Two wiring issues had to be resolved before the hardened TCK could execute
against the remote Payara. Both are recorded here because they are prerequisites
for anyone reproducing this run.

1. **Client-classpath API dependencies.** The TCK receives `jakarta.jakartaee-api`
   in `provided` scope from its parent POM, so it is *not* transitive to this
   runner. The new G1 validation tests reference `jakarta.validation`, which
   caused JUnit Platform discovery to abort with
   `NoClassDefFoundError: jakarta/validation/ConstraintViolationException`
   (aborting the entire suite). Added `jakarta.validation-api` (test scope) to the
   runner POM, mirroring the existing `cdi-api` and Yasson entries.

2. **Client-side implementation-present flag.** The deployment-error tests gate at
   the class level with `@EnabledIfSystemProperty(...implementation.present)`,
   which JUnit evaluates in the **client** JVM (the `@ShouldThrowException`
   deployment check runs before the in-container implementation probe). The flag
   was previously set only on the server, so all six deployment-error tests were
   silently skipped. Added
   `jakarta.ai.agent.tck.implementation.present=true` to the Failsafe
   `systemPropertyVariables` so these negative-deployment assertions actually run.

A third fix was needed in the TCK itself (spec repo, PR #74): the Arquillian
`AgenticAIFrameworkProcessor` did not deploy the
`ee.jakarta.tck.ai.agent.framework.workflow` package, so `WorkflowContext`
(referenced by `LargeLanguageModelStub`) was missing at runtime and produced 15
`NoClassDefFoundError` errors against a real implementation. Adding the package to
the processor cleared all 15.

## Summary for the ticket

- **API signature: conformant** (12/12).
- **Behavioral & lifecycle coverage: 176 pass.**
- **3 real gaps** to file against Payara: inherited-superclass `@Action` not
  invoked; two-phase-annotations-per-method not rejected; multi-parameter
  `@Trigger` not rejected.
- **4 deployment-error rules are enforced correctly** by Payara but are
  unverifiable through the remote adapter's exception wrapping — re-run under a
  managed/embedded adapter, or verify from `server.log`, to score them green.

## Resolution (post-fix)

The three genuine gaps were fixed in the Payara implementation
(`AgenticAIExtension.buildMetadata`, ticket FISH-14726, branch
`FISH-14726-agentic-ai-lifecycle-validation-gaps`):

1. **Inherited `@Action`** — `buildMetadata` now collects phase methods across
   the class hierarchy (most-derived first, overrides shadowing) instead of only
   `getDeclaredMethods()`, so an `@Action` inherited from a non-`@Agent`
   superclass participates.
2. **Two phase annotations per method** — a new `countPhaseAnnotations` check
   raises `DefinitionException` when a method carries more than one phase
   annotation.
3. **Multi-parameter `@Trigger`** — a new `countEventParameters` check raises
   `DefinitionException` when a `@Trigger` declares more than one event parameter.

Re-run against the patched build: **216 run, 177 pass, 0 fail, 6 errors, 33
skipped.** `server.log` confirms all six deployment-error agents are now rejected
with `jakarta.enterprise.inject.spi.DefinitionException` (including
`TwoPhaseMethodAgent … cannot declare more than one phase annotation` and
`MultiEventTriggerAgent … cannot declare more than one event parameter`), and
`InheritedPhaseTests` passes. The 6 remaining errors are exclusively the remote
adapter's exception-type masking described above, not implementation defects.
