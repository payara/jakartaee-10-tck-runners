package fish.payara.server.tck.agentic.arquillian;

import java.io.File;
import java.util.logging.Logger;

import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * Adds the AssertJ library to every TCK {@link WebArchive} before deployment.
 *
 * <p>The Agentic AI TCK's deployed tests assert with AssertJ, but the TCK's own
 * {@code AgenticAIFrameworkProcessor} only bundles the TCK framework packages.
 * AssertJ is on the runner's (out-of-container) classpath transitively through
 * the TCK jar, so the standalone tests pass, but it is missing from the WAR
 * classpath, which makes every deployed test fail with
 * {@code NoClassDefFoundError: org/assertj/core/api/Assertions}.</p>
 *
 * <p>This processor locates the AssertJ jar already on the runner classpath and
 * adds it as a {@code WEB-INF/lib} library. It runs in addition to (not instead
 * of) the TCK's framework processor.</p>
 */
public class AssertjArchiveProcessor implements ApplicationArchiveProcessor {

    private static final Logger LOGGER = Logger.getLogger(AssertjArchiveProcessor.class.getName());

    @Override
    public void process(Archive<?> archive, TestClass testClass) {
        if (!(archive instanceof WebArchive webArchive)) {
            return;
        }
        try {
            File assertjJar = new File(Assertions.class.getProtectionDomain()
                    .getCodeSource().getLocation().toURI());
            webArchive.addAsLibraries(assertjJar);
        } catch (Exception e) {
            LOGGER.warning("Could not add AssertJ library to archive " + archive.getName() + ": " + e);
        }
    }
}
