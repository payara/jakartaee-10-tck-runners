package fish.payara.tck;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * JUnit 5 extension that force-kills all Payara Micro processes after each test class.
 *
 * The Payara Micro managed Arquillian container calls Process.destroy() (SIGTERM) on undeploy
 * but doesn't wait for the process to exit. Grizzly keep-alive connections can delay shutdown
 * by up to 30 seconds, causing the next Micro instance to auto-bind to a different port than
 * the one hardcoded in webServerPort=8080.
 *
 * Some TCK test classes (e.g. JAXRSLocatorClientIT) have multiple @Deployment methods and
 * cause multiple concurrent Micro instances - killing only port 8080 is not enough.
 *
 * This extension's afterAll runs BEFORE Arquillian's afterAll (auto-detected ServiceLoader
 * extensions run in reverse order before @ExtendWith extensions), so the SIGKILL happens
 * before Arquillian calls undeploy. Arquillian's subsequent destroy() on the already-dead
 * processes is harmless.
 */
public class MicroPortCleanupExtension implements AfterAllCallback {

    private static final int MICRO_HTTP_PORT = 8080;
    private static final int WAIT_POLL_MS = 100;
    private static final int WAIT_TIMEOUT_MS = 15_000;

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        killAllPayaraMicroProcesses();
        waitForPortFree(MICRO_HTTP_PORT);
    }

    private void killAllPayaraMicroProcesses() throws IOException, InterruptedException {
        // Kill all JVM processes running payara-micro JAR files
        new ProcessBuilder("sh", "-c", "pkill -9 -f 'payara-micro.*\\.jar' 2>/dev/null || true")
                .start()
                .waitFor();
    }

    private void waitForPortFree(int port) throws InterruptedException {
        long deadline = System.currentTimeMillis() + WAIT_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            if (isPortFree(port)) return;
            Thread.sleep(WAIT_POLL_MS);
        }
    }

    private boolean isPortFree(int port) {
        try (ServerSocket ss = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
