package arquillian;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.config.descriptor.api.ExtensionDef;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import tck.arquillian.porting.lib.spi.AbstractTestArchiveProcessor;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;

public class GlassfishXmlProcessor extends AbstractTestArchiveProcessor {
    static HashSet<String> sunXmlFiles = new HashSet<String>();

    static {
        sunXmlFiles.add("META-INF/sun-application-client.xml");
        sunXmlFiles.add("META-INF/sun-application.xml");
        sunXmlFiles.add("META-INF/sun-ra.xml");
        sunXmlFiles.add("WEB-INF/sun-web.xml");
        sunXmlFiles.add("META-INF/sun-ejb-jar.xml");
    }

    private Path descriptorDirRoot;

    /**
     * Called on completion of the Arquillian configuration.
     */
    public void initalize(@Observes ArquillianDescriptor descriptor) {
        // Must call to setup the ResourceProvider
        super.initalize(descriptor);

        // Get the descriptor path
        ExtensionDef descriptorsDef = descriptor.extension("jboss-descriptors");
        String descriptorDir = descriptorsDef.getExtensionProperties().get("descriptorDir");
        if (descriptorDir == null) {
            String msg = "Specify the descriptorDir property in arquillian.xml as extension:\n" +
                    "<extension qualifier=\"jboss-descriptors\">\n" +
                    "        <property name=\"descriptorDir\">path-to-descriptors-dir</property>\n" +
                    "</extension>";
            //throw new IllegalStateException(msg);
        } else {
            this.descriptorDirRoot = Paths.get(descriptorDir);
            if (!Files.exists(this.descriptorDirRoot)) {
                //throw new RuntimeException("Descriptor directory does not exist: " + this.descriptorDirRoot);
            }
        }
    }

    @Override
    public void processClientArchive(JavaArchive clientArchive, Class<?> testClass, URL sunXmlURL) {
        String name = clientArchive.getName();
        addDescriptors(name, clientArchive, testClass);
    }

    @Override
    public void processWebArchive(WebArchive webArchive, Class<?> testClass, URL sunXmlURL) {
        String name = webArchive.getName();
        addDescriptors(name, webArchive, testClass);
    }

    @Override
    public void processRarArchive(JavaArchive warArchive, Class<?> testClass, URL sunXmlURL) {

    }

    @Override
    public void processParArchive(JavaArchive javaArchive, Class<?> aClass, URL url) {

    }

    @Override
    public void processEarArchive(EnterpriseArchive earArchive, Class<?> testClass, URL sunXmlURL) {
        String name = earArchive.getName();
        addDescriptors(name, earArchive, testClass);
    }

    @Override
    public void processEjbArchive(JavaArchive ejbArchive, Class<?> testClass, URL sunXmlURL) {
        String name = ejbArchive.getName();
        addDescriptors(name, ejbArchive, testClass);
    }

    /**
     * @param archiveName
     * @param archive
     * @param testClass
     */
    protected void addDescriptors(String archiveName, ManifestContainer<?> archive, Class<?> testClass) {
    }
}
