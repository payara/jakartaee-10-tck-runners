package fish.payara.tck.inject;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Named;
import jakarta.inject.Qualifier;
import org.atinject.tck.auto.Convertible;
import org.atinject.tck.auto.Drivers;
import org.atinject.tck.auto.DriversSeat;
import org.atinject.tck.auto.accessories.SpareTire;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * CDI portable extension that wires the Jakarta Inject TCK bean graph.
 *
 * The TCK bean classes carry no qualifier annotations themselves; this extension
 * adds them programmatically so CDI can resolve injection points unambiguously:
 *
 *  - DriversSeat gets @Drivers so it satisfies the @Drivers Car injection point in Convertible.
 *  - SpareTire gets @Named("spare") and @Spare to distinguish it from the plain Tire bean.
 *  - Convertible's spareTire field injection point gets @Spare to match the SpareTire bean.
 */
public class PayaraAtInjectTCKExtension implements Extension {

    public void convertible(@Observes ProcessAnnotatedType<Convertible> pat) {
        pat.configureAnnotatedType()
                .filterFields(field -> "spareTire".equals(field.getJavaMember().getName()))
                .forEach(field -> field.add(SpareLiteral.INSTANCE));
    }

    public void driversSeat(@Observes ProcessAnnotatedType<DriversSeat> pat) {
        pat.configureAnnotatedType().add(DriversLiteral.INSTANCE);
    }

    public void spareTire(@Observes ProcessAnnotatedType<SpareTire> pat) {
        pat.configureAnnotatedType()
                .add(new NamedLiteral("spare"))
                .add(SpareLiteral.INSTANCE);
    }

    // -- Qualifier & literals ------------------------------------------------

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    @interface Spare {
    }

    static final class DriversLiteral extends AnnotationLiteral<Drivers> implements Drivers {
        static final DriversLiteral INSTANCE = new DriversLiteral();

        private DriversLiteral() {
        }
    }

    static final class SpareLiteral extends AnnotationLiteral<Spare> implements Spare {
        static final SpareLiteral INSTANCE = new SpareLiteral();

        private SpareLiteral() {
        }
    }

    static final class NamedLiteral extends AnnotationLiteral<Named> implements Named {
        private final String value;

        NamedLiteral(String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }
}
