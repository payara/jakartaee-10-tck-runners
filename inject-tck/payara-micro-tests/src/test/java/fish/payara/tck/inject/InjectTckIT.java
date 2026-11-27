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
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import jakarta.enterprise.inject.spi.Extension;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

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

    /**
     * One JUnit 5 invocation per Inject TCK (JUnit 3) test case, so each case is reported individually.
     * Names are resolved without a Car instance; the case itself is executed in the container against the injected Car.
     */
    static Stream<String> tckCaseNames() {
        // Same cases as Tck.testsFor(car, false, true): the main tests plus the private member tests,
        // named like junit.framework.TestCase#toString
        return Stream.of(Convertible.Tests.class, Convertible.PrivateTests.class)
                .flatMap(c -> Arrays.stream(c.getMethods())
                        .filter(m -> m.getName().startsWith("test") && m.getParameterCount() == 0)
                        .map(m -> m.getName() + "(" + c.getName() + ")"))
                .sorted();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("tckCaseNames")
    public void injectTck(String caseName) {
        junit.framework.Test tckCase = flatten(Tck.testsFor(car, false, true), new ArrayList<>()).stream()
                .filter(t -> caseName.equals(String.valueOf(t)))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Inject TCK case not found: " + caseName));

        junit.framework.TestResult result = new junit.framework.TestResult();
        tckCase.run(result);
        if (result.runCount() != 1 || result.failureCount() > 0 || result.errorCount() > 0) {
            StringBuilder sb = new StringBuilder("Jakarta Inject TCK failure in " + caseName + ":\n");
            Collections.list(result.failures())
                    .forEach(f -> sb.append("  FAILURE: ").append(f).append('\n'));
            Collections.list(result.errors())
                    .forEach(e -> sb.append("  ERROR: ").append(e).append('\n'));
            Assertions.fail(sb.toString());
        }
    }

    private static List<junit.framework.Test> flatten(junit.framework.Test test, List<junit.framework.Test> out) {
        if (test instanceof junit.framework.TestSuite) {
            Collections.list(((junit.framework.TestSuite) test).tests()).forEach(t -> flatten(t, out));
        } else {
            out.add(test);
        }
        return out;
    }
}
