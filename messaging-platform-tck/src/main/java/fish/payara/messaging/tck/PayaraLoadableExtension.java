package fish.payara.messaging.tck;

import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.test.spi.enricher.resource.ResourceProvider;

public class PayaraLoadableExtension implements LoadableExtension {
    @Override
    public void register (ExtensionBuilder extensionBuilder) {
        extensionBuilder.service(ResourceProvider.class, PayaraTestArchiveProcessor.class);
        extensionBuilder.observer(PayaraTestArchiveProcessor.class);
    }
}
