package fish.payara.server.tck.agentic.arquillian;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.spi.LoadableExtension;

/**
 * Registers the runner-side archive processors for the Agentic AI TCK.
 *
 * <p>This is additive to the TCK's own {@code AgenticAILoadableExtension}: both
 * are discovered via the ServiceLoader and all their processors run.</p>
 */
public class AgenticArquillianExtension implements LoadableExtension {

    @Override
    public void register(ExtensionBuilder builder) {
        builder.service(ApplicationArchiveProcessor.class, AssertjArchiveProcessor.class);
    }
}
