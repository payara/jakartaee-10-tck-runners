package fish.payara.server.tck.jsonp.api;

import ee.jakarta.tck.jsonp.api.common.SimpleValues;
import ee.jakarta.tck.jsonp.api.jsonwritertests.ClientTests;
import ee.jakarta.tck.jsonp.common.JSONP_Util;
import ee.jakarta.tck.jsonp.common.MyBufferedWriter;
import ee.jakarta.tck.jsonp.provider.MyJsonProvider;
import jakarta.json.spi.JsonProvider;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class JsonPWriterTest extends  ClientTests {

    @Deployment
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, ClientTests.class.getPackage().getName())
                .addPackages(true, MyBufferedWriter.class.getPackage().getName())
                .addPackages(true, SimpleValues.class.getPackage().getName());
    }
}
