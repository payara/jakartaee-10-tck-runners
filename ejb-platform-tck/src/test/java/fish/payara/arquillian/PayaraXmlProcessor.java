package fish.payara.arquillian;

import java.net.URL;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import tck.arquillian.porting.lib.spi.AbstractTestArchiveProcessor;

public class PayaraXmlProcessor extends AbstractTestArchiveProcessor {
    /**
     * Called on completion of the Arquillian configuration.
     */
    @Override
    public void initalize(@Observes ArquillianDescriptor descriptor) {
        // Must call to setup the ResourceProvider
        super.initalize(descriptor);
    }

    @Override
    public void processClientArchive(JavaArchive clientArchive, Class<?> testClass, URL sunXmlURL) {
        // Do nothing - TCK runner complains if this class doesn't exist
    }

    @Override
    public void processWebArchive(WebArchive webArchive, Class<?> testClass, URL sunXmlURL) {
        // Do nothing - TCK runner complains if this class doesn't exist
    }

    @Override
    public void processRarArchive(JavaArchive warArchive, Class<?> testClass, URL sunXmlURL) {
        // Do nothing - TCK runner complains if this class doesn't exist
    }

    @Override
    public void processParArchive(JavaArchive javaArchive, Class<?> aClass, URL url) {
        // Do nothing - TCK runner complains if this class doesn't exist
    }

    @Override
    public void processEarArchive(EnterpriseArchive earArchive, Class<?> testClass, URL sunXmlURL) {
        // Do nothing - TCK runner complains if this class doesn't exist
    }

    @Override
    public void processEjbArchive(JavaArchive ejbArchive, Class<?> testClass, URL sunXmlURL) {
        // Do nothing - TCK runner complains if this class doesn't exist
    }
}
