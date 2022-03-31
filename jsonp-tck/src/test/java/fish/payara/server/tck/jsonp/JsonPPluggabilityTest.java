package fish.payara.server.tck.jsonp;

import ee.jakarta.tck.jsonp.common.JSONP_Util;
import jakarta.json.spi.JsonProvider;
import ee.jakarta.tck.jsonp.provider.MyJsonProvider;
import ee.jakarta.tck.jsonp.pluggability.jsonprovidertests.ClientTests;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class JsonPPluggabilityTest extends ClientTests {

    @Deployment
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, ClientTests.class.getPackage().getName())
                .addPackages(true, MyJsonProvider.class.getPackage().getName())
                .addPackages(true, JSONP_Util.class.getPackage().getName())
                .addAsServiceProvider(JsonProvider.class, MyJsonProvider.class);
    }

}
