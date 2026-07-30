/*
 * Copyright (c) 2026 Payara Foundation and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Java replacement for {@code run-tck.sh} that can drive the Jakarta XML Binding
 * TCK in three execution modes:
 *
 * <ul>
 *   <li>{@code multi-jvm}    – the legacy default: a fresh JVM is spawned per
 *       schema compile and per test execution (via the bundled shell scripts).</li>
 *   <li>{@code single-agent} – one long-lived JavaTest agent JVM runs every test
 *       in-process, with {@code concurrency} tests at a time. XJC/schemagen run
 *       in-process (no shell scripts). Much faster; shares one heap.</li>
 *   <li>{@code agent-pool}   – N long-lived agent JVMs, each single-threaded.
 *       JVM reuse <em>with</em> process isolation; the safest fast mode.</li>
 * </ul>
 *
 * <p>Run as a single-file source program:
 * {@code java RunTck.java <mode> <payara-home> <concurrency> <report-dir> [<progress>] [<interval-sec>]}
 * where {@code concurrency == 0} means "auto-detect from available processors",
 * {@code progress} is {@code true/false} (default {@code false}),
 * and {@code interval-sec} is the reporting interval in seconds (default 60).
 *
 * <p>See {@code src/docs/developer/vendor-speedup-guide.adoc} in the TCK sources
 * for the rationale behind the agent modes and the shared-state caveats.
 */
public class RunTck {

    /** TCK unpack directory name under {@code target/}. */
    private static final String TCK_NAME = "xml-binding-tck";

    // -----------------------------------------------------------------------
    // JAXB implementation JARs supplied by Payara (glassfish/modules/).
    // These are the most likely things to change when upgrading Payara or
    // switching to a different JAXB implementation.
    // -----------------------------------------------------------------------
    private static final List<String> SERVER_JARS = List.of(
            "jakarta.xml.bind-api.jar",
            "jaxb-osgi.jar",
            "jersey-media-jaxb.jar",
            "jakarta.activation-api.jar"
    );
    /** CheckerFramework JAR — required by the TCK signature tests. Lives in target/, not modules/. */
    private static final String JAR_CHECKER = "checker.jar";

    enum Mode {
        MULTI_JVM("multi-jvm", "javasoft-multiJVM.jti", "batch-multiJVM"),
        SINGLE_AGENT("single-agent", "javasoft-singleJVM.jti", "batch-singleJVM"),
        AGENT_POOL("agent-pool", "javasoft-singleJVM.jti", "batch-agentPool");

        final String id;
        final String jti;
        final String batchDir;

        Mode(String id, String jti, String batchDir) {
            this.id = id;
            this.jti = jti;
            this.batchDir = batchDir;
        }

        static Mode of(String id) {
            for (Mode m : values()) {
                if (m.id.equals(id)) {
                    return m;
                }
            }
            throw new IllegalArgumentException("Unknown mode '" + id
                    + "'. Expected one of: multi-jvm, single-agent, agent-pool");
        }
    }

    private final Mode mode;
    private final Path payaraHome;
    private final Path glassfishHome;
    private final int requestedConcurrency;
    private final Path reportDir;

    private final Path workspace;        // .../xml-binding-tck/target
    private final Path tckDir;           // .../target/xml-binding-tck
    private final Path jtiFile;          // .../target/xml-binding-tck/lib/<mode jti>
    private final Path javatestJar;      // .../target/xml-binding-tck/lib/javatest.jar
    private final Path batchWork;        // .../target/<batchDir>/work

    /** Whether to print a pass/fail summary at regular intervals while tests run. */
    private final boolean progress;
    /** How often to print the progress summary, in seconds. */
    private final int progressIntervalSec;

    RunTck(String[] args) {
        if (args.length < 4) {
            throw new IllegalArgumentException(
                    "Usage: java RunTck.java <mode> <payara-home> <concurrency> <report-dir>"
                    + " [<progress:true|false>] [<interval-sec>]");
        }
        this.mode = Mode.of(args[0]);
        this.payaraHome = Path.of(args[1]).toAbsolutePath().normalize();
        this.glassfishHome = payaraHome.resolve("glassfish");
        this.requestedConcurrency = Integer.parseInt(args[2].trim());
        this.reportDir = Path.of(args[3]).toAbsolutePath().normalize();
        this.progress = args.length >= 5 && Boolean.parseBoolean(args[4].trim());
        this.progressIntervalSec = args.length >= 6 ? Integer.parseInt(args[5].trim()) : 60;

        // The script lives in the module root; target/ is its working area.
        Path moduleDir = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        this.workspace = moduleDir.resolve("target");
        this.tckDir = workspace.resolve(TCK_NAME);
        this.jtiFile = tckDir.resolve("lib").resolve(mode.jti);
        this.javatestJar = tckDir.resolve("lib").resolve("javatest.jar");
        this.batchWork = workspace.resolve(mode.batchDir).resolve("work");
    }

    public static void main(String[] args) throws Exception {
        RunTck runner = new RunTck(args);
        runner.run();
    }

    private void run() throws Exception {
        System.out.println("=== Jakarta XML Binding TCK runner ===");
        System.out.println("mode        : " + mode.id);
        System.out.println("payara home : " + payaraHome);
        System.out.println("concurrency : " + effectiveConcurrency()
                + (requestedConcurrency == 0 ? " (auto-detected)" : ""));
        System.out.println("report dir  : " + reportDir);
        System.out.println("workspace   : " + workspace);
        System.out.println("progress    : " + (progress
                ? "enabled (every " + progressIntervalSec + "s)" : "disabled"));

        if (mode == Mode.MULTI_JVM && isWindows()) {
            throw new UnsupportedOperationException(
                    "multi-jvm mode relies on the bundled shell scripts and cannot run on Windows. "
                    + "Use -Psingle-agent or -Pagent-pool instead.");
        }

        patchTestsuiteFinder();

        // Guard used by both the shutdown hook and the normal finally path to
        // ensure report generation runs exactly once — whichever gets there first.
        AtomicBoolean reportDone = new AtomicBoolean(false);

        // Generate a partial JUnit report when the process is killed with SIGINT
        // (Ctrl+C).  SIGINT also kills the child JavaTest process, so waitFor()
        // returns, the finally block runs — but if the JVM itself has started
        // shutting down before the finally completes, the hook is the safety net.
        Thread exitHook = new Thread(() -> {
            if (reportDone.compareAndSet(false, true)) {
                System.out.println("\n[exit] Writing partial report...");
                try { writeReport(); } catch (Exception e) { System.err.println(e.getMessage()); }
                try { convertToJUnit(); } catch (Exception e) { System.err.println(e.getMessage()); }
            }
        }, "tck-exit-report");
        Runtime.getRuntime().addShutdownHook(exitHook);

        Thread readerThread   = null;
        Thread reporterThread = null;
        ProgressReporter reporter = null;
        if (progress) {
            BlockingQueue<String> queue = new LinkedBlockingQueue<>();
            reporter      = new ProgressReporter(queue, progressIntervalSec);
            readerThread  = new Thread(
                    new TraceReader(batchWork.resolve("jtData").resolve("harness.trace"), queue),
                    "tck-trace-reader");
            reporterThread = new Thread(reporter, "tck-progress-reporter");
            readerThread.setDaemon(true);
            reporterThread.setDaemon(true);
            readerThread.start();
            reporterThread.start();
        }
        try {
            switch (mode) {
                case MULTI_JVM -> runMultiJvm();
                case SINGLE_AGENT -> runAgents(1, effectiveConcurrency()*2);  // 3x for single jvm. Switch to agentpool if you see concurrency issues.
                case AGENT_POOL -> runAgents(effectiveConcurrency(), 1);
            }
        } finally {
            if (readerThread != null) {
                readerThread.interrupt();
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                reporter.report();
                reporterThread.interrupt();
            }
            // Deregister the hook; if the JVM is already shutting down the
            // removeShutdownHook call itself will throw — that means the hook
            // will handle report generation, so we skip it here.
            boolean hookRemoved = false;
            try {
                hookRemoved = Runtime.getRuntime().removeShutdownHook(exitHook);
            } catch (IllegalStateException ignored) { /* JVM already shutting down */ }

            if (hookRemoved && reportDone.compareAndSet(false, true)) {
                writeReport();
                convertToJUnit();
                archive();
                System.out.println("=== TCK run complete: report at " + reportDir + " ===");
            }
        }
    }

    // ------------------------------------------------------------------
    // Mode runners
    // ------------------------------------------------------------------

    private void runMultiJvm() throws Exception {
        Map<String, String> p = new LinkedHashMap<>();
        p.put("TESTSUITE", tckDir + "/");
        p.put("WORKDIR", batchWork + "/");
        p.put("jck.env.jaxb.testExecute.cmdAsFile", javaExecutable().toString());
        p.put("jck.env.jaxb.testExecute.otherEnvVars", envVars());
        p.put("jck.env.jaxb.xsd_compiler.testCompile.xjcCmd", xjcCmd());
        p.put("jck.env.jaxb.schemagen.run.jxcCmd", schemagenCmd());
        p.put("jck.env.jaxb.classes.jaxbClasses", jaxbClassesSpaceSeparated());
        p.put("jck.env.jaxb.classes.needJaxbClasses", "Yes");
        p.put("jck.concurrency.concurrency", Integer.toString(effectiveConcurrency()));
        patchProperties(jtiFile, p);

        runTests(/* create */ true, /* notRunOnly */ false, /* agentPort */ -1);
        runTests(/* create */ false, /* notRunOnly */ true, /* agentPort */ -1);
    }

    /**
     * Agent-based execution (single-agent and agent-pool share this path).
     *
     * @param agentCount       number of agent JVMs to start
     * @param agentConcurrency tests each agent runs at once
     */
    private void runAgents(int agentCount, int agentConcurrency) throws Exception {
        int harnessConcurrency = agentCount * agentConcurrency;

        Map<String, String> p = new LinkedHashMap<>();
        p.put("TESTSUITE", tckDir + "/");
        p.put("WORKDIR", batchWork + "/");
        p.put("jck.env.jaxb.testExecute.cmdAsFile", javaExecutable().toString());
        p.put("jck.env.jaxb.testExecute.otherEnvVars", envVars());
        p.put("jck.concurrency.concurrency", Integer.toString(harnessConcurrency));
        patchProperties(jtiFile, p);

        int port = findFreePort();
        System.out.println("agent pool port : " + port);
        System.out.println("starting " + agentCount + " agent JVM(s), concurrency "
                + agentConcurrency + " each");

        List<Process> agents = startAgents(port, agentCount, agentConcurrency);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Process a : agents) {
                a.destroy();
            }
        }));

        // Give the agents a moment to connect to the pool before JavaTest starts
        // dispatching work to it.
        Thread.sleep(2000L);

        try {
            runTests(/* create */ true, /* notRunOnly */ false, port);
            runTests(/* create */ false, /* notRunOnly */ true, port);
        } finally {
            for (Process a : agents) {
                a.destroy();
            }
        }
    }

    private List<Process> startAgents(int port, int agentCount, int agentConcurrency)
            throws IOException {
        String agentCp = String.join(File.pathSeparator,
                javatestJar.toString(),
                tckDir.resolve("classes").toString(),
                jaxbClassesPathSeparated(),
                workspace.resolve("checker.jar").toString());

        // Suppress the chatty JUL INFO messages from DirsClassLoader.newInstance()
        // (and any other verbose INFO logging in the agent JVMs) by writing a
        // minimal logging.properties that raises the console threshold to WARNING.
        Path loggingConfig = workspace.resolve("agent-logging.properties");
        if (!Files.exists(loggingConfig)) {
            Files.writeString(loggingConfig,
                    "handlers=java.util.logging.ConsoleHandler\n"
                    + ".level=WARNING\n"
                    + "java.util.logging.ConsoleHandler.level=WARNING\n"
                    + "java.util.logging.ConsoleHandler.formatter=java.util.logging.SimpleFormatter\n");
        }

        List<Process> agents = new ArrayList<>();
        for (int i = 0; i < agentCount; i++) {
            List<String> cmd = new ArrayList<>();
            cmd.add(javaExecutable().toString());
            cmd.add("-cp");
            cmd.add(agentCp);
            cmd.add("-Djava.security.policy=" + tckDir.resolve("lib").resolve("tck.policy"));
            cmd.add("-Djava.util.logging.config.file=" + loggingConfig);
            // --- Java 21 tuning (uncomment to enable) ---
            // Reuse an application class-data archive to cut agent start-up cost.
            // cmd.add("-XX:+AutoCreateSharedArchive");
            // cmd.add("-XX:SharedArchiveFile=" + workspace.resolve("jaxbtck-agent-" + i + ".jsa"));
            // Reclaim per-test class loaders concurrently instead of the harness's
            // periodic System.gc() crutch.
            // cmd.add("-XX:+ClassUnloadingWithConcurrentMark");
            cmd.add("com.sun.javatest.agent.AgentMain");
            cmd.add("-activeHost");
            cmd.add("localhost");
            cmd.add("-activePort");
            cmd.add(Integer.toString(port));
            cmd.add("-concurrency");
            cmd.add(Integer.toString(agentConcurrency));

            // Run agents from the signaturetest directory so that SigTestWrapper
            // can resolve the bare relative path "sig/jakarta.xml.bind.sig" it
            // constructs.  All other tests receive absolute paths via the harness
            // environment macros, so the cwd does not affect them.
            ProcessBuilder pb = new ProcessBuilder(cmd)
                    .directory(tckDir.resolve("tests").resolve("api")
                                     .resolve("signaturetest").toFile())
                    .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .redirectError(ProcessBuilder.Redirect.INHERIT);
            pb.environment().put("JAVA_HOME", javaHome().toString());
            pb.environment().put("JAXB_HOME", glassfishHome.toString());
            // SchemaGenerator (schemagen / jxc) reads Options.classpath from
            // System.getenv("CLASSPATH") on Java 11+ where AppClassLoader is
            // no longer a URLClassLoader and the TCCL walk in setClasspath()
            // finds nothing.  Explicitly setting CLASSPATH here ensures that
            // jakarta.activation-api.jar (and everything else) is visible to
            // javac when schemagen compiles the Java sources in-process.
            pb.environment().put("CLASSPATH", agentCp);
            agents.add(pb.start());
        }
        return agents;
    }

    // ------------------------------------------------------------------
    // JavaTest invocation
    // ------------------------------------------------------------------

    private void runTests(boolean createWorkDir, boolean notRunOnly, int agentPort)
            throws Exception {
        List<String> cmd = new ArrayList<>();
        cmd.add(javaExecutable().toString());
        cmd.add("-jar");
        cmd.add(javatestJar.toString());
        cmd.add("-batch");
        cmd.add("-testsuite");
        cmd.add(tckDir.toString());
        cmd.add("-open");
        cmd.add(jtiFile.toString());

        if (agentPort > 0) {
            cmd.add("-startAgentPool");
            cmd.add("-agentPoolPort");
            cmd.add(Integer.toString(agentPort));
        }

        cmd.add("-workdir");
        if (createWorkDir) {
            cmd.add("-create");
        }
        cmd.add(batchWork.toString());

        cmd.add("-set");
        cmd.add("jck.env.jaxb.xsd_compiler.skipValidationOptional");
        cmd.add("Yes");

        if (notRunOnly) {
            cmd.add("-set");
            cmd.add("jck.priorStatus.needStatus");
            cmd.add("Yes");
            cmd.add("-set");
            cmd.add("jck.priorStatus.status");
            cmd.add("not_run");
        }

        cmd.add("-runtests");

        System.out.println((notRunOnly ? "--- JavaTest pass 2 (rerun not_run) ---"
                                       : "--- JavaTest pass 1 (full run) ---"));

        // run-tck.sh cds into tests/api/signaturetest/ before launching JavaTest.
        // SigTestWrapper builds a bare relative path  "sig/jakarta.xml.bind.sig"
        // and passes it straight to the sigtest tool, so the JVM working directory
        // must be that directory for the path to resolve.  All other tests receive
        // absolute paths via $testSuiteRootDir / TestURL, so this is harmless.
        exec(cmd, tckDir.resolve("tests").resolve("api").resolve("signaturetest"));
    }

    private void writeReport() throws Exception {
        Files.createDirectories(reportDir.resolve("JAXB-TCK"));
        exec(List.of(
                javaExecutable().toString(),
                "-jar", javatestJar.toString(),
                "-workdir", batchWork.toString(),
                "-writereport", reportDir.resolve("JAXB-TCK").toString()));
    }

    private void convertToJUnit() throws Exception {
        Path parser = workspace.getParent().resolve("JTReportParser.jar");
        if (!Files.exists(parser)) {
            System.out.println("JTReportParser.jar not found at " + parser + " - skipping JUnit conversion");
            return;
        }
        Path junitDir = workspace.resolve("results").resolve("junitreports");
        Files.createDirectories(junitDir);

        String host = hostname();
        Path argsFile = workspace.resolve("args.txt");
        Files.writeString(argsFile, "1 JAXB-TCK " + host + System.lineSeparator());

        exec(List.of(
                javaExecutable().toString(),
                "-Djunit.embed.sysout=true",
                "-jar", parser.toString(),
                argsFile.toString(),
                reportDir.toString(),
                junitDir.toString()));

        Files.deleteIfExists(argsFile);
    }

    private void archive() throws Exception {
        Path tarball = workspace.resolve("jaxbtck-" + mode.id + "-results.tar.gz");
        List<String> cmd = new ArrayList<>(List.of(
                "tar", "zcf", tarball.toString(),
                reportDir.toString(),
                batchWork.getParent().toString()));
        Path junit = workspace.resolve("results").resolve("junitreports");
        if (Files.exists(junit)) {
            cmd.add(junit.toString());
        }
        try {
            exec(cmd);
        } catch (Exception e) {
            System.out.println("Archiving failed (non-fatal): " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Progress monitoring — producer / consumer
    // ------------------------------------------------------------------

    /**
     * Continuously tails {@code harness.trace} and puts each new line into the
     * shared queue. Blocks for 100 ms when at the current EOF so it does not
     * busy-spin. Uses {@link RandomAccessFile} because {@link java.io.BufferedReader}
     * / {@link java.io.InputStreamReader} cache the EOF state and stop retrying
     * once they see it, which makes them unsuitable for tailing a growing file.
     */
    private static class TraceReader implements Runnable {
        private final Path trace;
        private final BlockingQueue<String> queue;

        TraceReader(Path trace, BlockingQueue<String> queue) {
            this.trace = trace;
            this.queue = queue;
        }

        @Override
        public void run() {
            // Wait until JavaTest creates the file (typically within the first few seconds).
            while (!Thread.currentThread().isInterrupted() && !Files.exists(trace)) {
                try { Thread.sleep(500); } catch (InterruptedException e) { return; }
            }
            if (Thread.currentThread().isInterrupted()) return;

            try (RandomAccessFile raf = new RandomAccessFile(trace.toFile(), "r")) {
                while (!Thread.currentThread().isInterrupted()) {
                    String line = raf.readLine();
                    if (line != null) {
                        queue.put(line);
                    } else {
                        Thread.sleep(100);   // at EOF — wait for more content
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                System.out.println("[progress] trace reader error: " + e.getMessage());
            }
        }
    }

    /**
     * Drains the shared line queue every {@code intervalSec} seconds, updates
     * cumulative pass/fail/error counters, and prints a one-line summary plus any
     * new failures recorded in that interval. Suppresses output when nothing
     * has changed since the last print (so the final flush after the run does not
     * produce a duplicate line).
     */
    private static class ProgressReporter implements Runnable {
        private final BlockingQueue<String> queue;
        private final int intervalSec;

        private long totalPassed    = 0;
        private long totalFailed    = 0;
        private long totalError     = 0;
        private long lastPrintTotal = 0;   // 0 = nothing printed yet; suppresses empty early ticks

        ProgressReporter(BlockingQueue<String> queue, int intervalSec) {
            this.queue = queue;
            this.intervalSec = intervalSec;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(intervalSec * 1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                report();
            }
        }

        /** Drain the queue, update counters, print if anything changed. */
        void report() {
            List<String> batch = new ArrayList<>();
            queue.drainTo(batch);

            List<String> newFailures = new ArrayList<>();
            for (String line : batch) {
                if (!line.contains("Finished:")) continue;
                if      (line.contains(": Passed")) { totalPassed++; }
                else if (line.contains(": Failed")) { totalFailed++; newFailures.add(line); }
                else if (line.contains(": Error"))  { totalError++;  newFailures.add(line); }
            }

            long total = totalPassed + totalFailed + totalError;
            if (total == 0 || total == lastPrintTotal) return;   // nothing to show
            lastPrintTotal = total;

            System.out.printf("[progress %s]  passed=%-6d  failed=%-4d  error=%-4d  total=%d%n",
                    LocalTime.now().withNano(0), totalPassed, totalFailed, totalError, total);

            for (String line : newFailures) {
                // Strip leading timestamp; keep "Finished: (N s) path: Failed. reason"
                int idx = line.indexOf(" Finished:");
                System.out.println("  FAIL: " + (idx >= 0 ? line.substring(idx + 1) : line));
            }
        }
    }

    // ------------------------------------------------------------------
    // File patching
    // ------------------------------------------------------------------

    /** Patch the {@code finder=} line of {@code testsuite.jtt} to the unpacked location. */
    private void patchTestsuiteFinder() throws IOException {
        Path jtt = tckDir.resolve("testsuite.jtt");
        String finder = "com.sun.javatest.finder.BinaryTestFinder -binary "
                + tckDir.resolve("tests").resolve("testsuite.jtd");
        patchProperties(jtt, Map.of("finder", finder));
    }

    /**
     * Replace {@code key=...} lines in a {@code key=value} file, preserving order,
     * comments and any keys not mentioned. Keys absent from the file are appended.
     */
    private void patchProperties(Path file, Map<String, String> replacements) throws IOException {
        List<String> lines = Files.readAllLines(file);
        Map<String, String> remaining = new LinkedHashMap<>(replacements);
        List<String> out = new ArrayList<>(lines.size());

        for (String line : lines) {
            String replaced = null;
            for (Map.Entry<String, String> e : replacements.entrySet()) {
                if (line.startsWith(e.getKey() + "=")) {
                    replaced = e.getKey() + "=" + e.getValue();
                    remaining.remove(e.getKey());
                    break;
                }
            }
            out.add(replaced != null ? replaced : line);
        }
        // Append any keys that were not already present.
        for (Map.Entry<String, String> e : remaining.entrySet()) {
            out.add(e.getKey() + "=" + e.getValue());
        }

        Files.write(file, out);
        System.out.println("patched " + file.getFileName() + ": " + replacements.keySet());
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private int effectiveConcurrency() {
        return requestedConcurrency > 0
                ? requestedConcurrency
                : Runtime.getRuntime().availableProcessors();
    }

    private int findFreePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    /** JAXB implementation JARs, space-separated (JTI {@code jaxbClasses} format). */
    private String jaxbClassesSpaceSeparated() {
        return String.join(" ", jaxbJars());
    }

    /** JAXB implementation JARs, path-separator-separated (JVM {@code -cp} format). */
    private String jaxbClassesPathSeparated() {
        return String.join(File.pathSeparator, jaxbJars());
    }

    private List<String> jaxbJars() {
        Path modules = glassfishHome.resolve("modules");
        List<String> jars = new ArrayList<>();
        for (String jar : SERVER_JARS) {
            jars.add(modules.resolve(jar).toString());
        }
        jars.add(workspace.resolve(JAR_CHECKER).toString());
        return jars;
    }

    /** {@code otherEnvVars} value in JTI format: {@code =} inside values is escaped as {@code \=}. */
    private String envVars() {
        return "JAVA_HOME\\=" + javaHome() + " JAXB_HOME\\=" + glassfishHome;
    }

    private String xjcCmd() {
        return osShell() + tckDir.resolve(osBinDir()).resolve(osScript("xjc"));
    }

    private String schemagenCmd() {
        return osShell() + tckDir.resolve(osBinDir()).resolve(osScript("schemagen"));
    }

    private String osBinDir() {
        String os = osName();
        if (os.contains("win")) {
            return "win32/bin";
        }
        if (os.contains("mac") || os.contains("darwin")) {
            return "macos/bin";
        }
        if (os.contains("sunos") || os.contains("solaris")) {
            return "solaris/bin";
        }
        return "linux/bin";
    }

    private String osScript(String tool) {
        return isWindows() ? tool + ".bat" : tool + ".sh";
    }

    /** Shell prefix for invoking the tool script (empty on Windows, which runs .bat directly). */
    private String osShell() {
        if (isWindows()) {
            return "";
        }
        String os = osName();
        return (os.contains("sunos") || os.contains("solaris")) ? "/bin/ksh " : "/bin/sh ";
    }

    private Path javaHome() {
        return Path.of(System.getProperty("java.home"));
    }

    private Path javaExecutable() {
        return javaHome().resolve("bin").resolve(isWindows() ? "java.exe" : "java");
    }

    private String hostname() {
        try {
            return InetAddress.getLocalHost().getCanonicalHostName();
        } catch (Exception e) {
            return "localhost";
        }
    }

    private static String osName() {
        return System.getProperty("os.name").toLowerCase();
    }

    private static boolean isWindows() {
        return osName().contains("win");
    }

    private void exec(List<String> cmd) throws Exception {
        exec(cmd, workspace);
    }

    private void exec(List<String> cmd, Path workDir) throws Exception {
        System.out.println("+ " + String.join(" ", cmd));
        ProcessBuilder pb = new ProcessBuilder(cmd)
                .directory(workDir.toFile())
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT);
        pb.environment().put("JAVA_HOME", javaHome().toString());
        pb.environment().put("JAXB_HOME", glassfishHome.toString());
        pb.environment().put("JAVA_TOOL_OPTIONS", "-Dfile.encoding=UTF8");
        int rc = pb.start().waitFor();
        if (rc != 0) {
            System.out.println("command exited with code " + rc);
        }
    }
}
