package fish.payara.server.tck.jsonb.api;

import ee.jakarta.tck.json.bind.TypeContainer;
import ee.jakarta.tck.json.bind.api.model.SimpleContainer;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
public class JsonbTest extends ee.jakarta.tck.json.bind.api.jsonb.JsonbTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, ee.jakarta.tck.json.bind.api.jsonb.JsonbTest.class.getPackage().getName())
                .addPackages(true, SimpleContainer.class.getPackage().getName())
                .addPackages(true, TypeContainer.class.getPackage().getName())
                .addPackages(true, Matchers.class.getPackage());
    }
}
