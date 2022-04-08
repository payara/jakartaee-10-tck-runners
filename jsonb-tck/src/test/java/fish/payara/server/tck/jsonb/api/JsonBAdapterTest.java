package fish.payara.server.tck.jsonb.api;

import ee.jakarta.tck.json.bind.TypeContainer;
import ee.jakarta.tck.json.bind.api.jsonbadapter.JsonbAdapterTest;
import ee.jakarta.tck.json.bind.api.model.SimpleStringAdapter;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class JsonBAdapterTest extends JsonbAdapterTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, JsonbAdapterTest.class.getPackage().getName())
                .addPackages(true, SimpleStringAdapter.class.getPackage().getName())
                .addPackages(true, TypeContainer.class.getPackage().getName())
                .addPackages(true, Matchers.class.getPackage());
    }
}
