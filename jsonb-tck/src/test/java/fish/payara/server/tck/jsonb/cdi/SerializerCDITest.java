package fish.payara.server.tck.jsonb.cdi;

import ee.jakarta.tck.json.bind.cdi.customizedmapping.serializers.SerializersCustomizationCDITest;
import ee.jakarta.tck.json.bind.customizedmapping.serializers.model.Animal;
import ee.jakarta.tck.json.bind.customizedmapping.serializers.model.serializer.AnimalBuilder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@ExtendWith(InvocationExtension.class)
public class SerializerCDITest extends  SerializersCustomizationCDITest {

    @Deployment
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, SerializersCustomizationCDITest.class.getPackage().getName())
                .addPackages(true, Animal.class.getPackage().getName())
                .addPackages(true, AnimalBuilder.class.getPackage().getName());
    }
}
