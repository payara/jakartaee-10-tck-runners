package fish.payara.tck.inject;

import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;
import org.atinject.tck.auto.Convertible;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.enterprise.inject.spi.Extension;
import jakarta.inject.Inject;
import java.util.Collections;

@ExtendWith(ArquillianExtension.class)
public class InjectTckIT {

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class, "inject-tck.war")
                .addPackages(true, "org.atinject.tck")
                .addClass(PayaraAtInjectTCKExtension.class)
                .addAsServiceProvider(Extension.class, PayaraAtInjectTCKExtension.class)
                .addAsLibraries(Maven.resolver()
                        .resolve("junit:junit:4.13.2")
                        .withTransitivity()
                        .asFile())
                .addAsWebInfResource(new StringAsset(
                        "<beans xmlns=\"https://jakarta.ee/xml/ns/jakartaee\" " +
                        "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                        "xsi:schemaLocation=\"https://jakarta.ee/xml/ns/jakartaee " +
                        "https://jakarta.ee/xml/ns/jakartaee/beans_4_0.xsd\" " +
                        "version=\"4.0\" bean-discovery-mode=\"all\"/>"), "beans.xml");
    }

    @Inject
    Car car;

    @Test
    public void injectTckTests() {
        runAndAssert(Tck.testsFor(car, false, true));
    }

    @Test
    public void injectTckPrivateTests() {
        // Convertible.PrivateTests: 4 cases covering private member injection
        Convertible.localConvertible.set((Convertible) car);
        junit.framework.TestSuite suite;
        try {
            suite = new junit.framework.TestSuite(Convertible.PrivateTests.class);
        } finally {
            Convertible.localConvertible.remove();
        }
        runAndAssert(suite);
    }

    private void runAndAssert(junit.framework.Test suite) {
        junit.framework.TestResult result = new junit.framework.TestResult();
        suite.run(result);
        if (result.failureCount() > 0 || result.errorCount() > 0) {
            StringBuilder sb = new StringBuilder("Jakarta Inject TCK failures:\n");
            Collections.list(result.failures())
                    .forEach(f -> sb.append("  FAILURE: ").append(f).append('\n'));
            Collections.list(result.errors())
                    .forEach(e -> sb.append("  ERROR: ").append(e).append('\n'));
            Assertions.fail(sb.toString());
        }
    }
}
