# Jakarta REST TCK - Payara Micro Tests

Runs the Jakarta REST TCK against Payara Micro using the Arquillian Payara Micro Managed container.

## Prerequisites

JDK 11 or later is required. Download and install the TCK from the root directory:

```bash
mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-rest-tck
```

## Running

From the root directory (`-am` includes the root project, which downloads Payara Micro):

```bash
mvn verify -Ppayara-micro-managed -pl rest-tck/payara-micro-tests -am
```

To override the Payara version:

```bash
mvn verify -Ppayara-micro-managed -pl rest-tck/payara-micro-tests -am -Dpayara.version=<version>
```

## Notes

- The `payara-micro-managed` profile downloads `payara-micro-<version>.jar` to `<root>/target/` and passes it to the test container via `payara.micro.jar`.
- The following test classes are excluded as they require SE Bootstrap or RxInvoker support not applicable to Payara Micro:
  - `SeBootstrapIT`
  - `ee.jakarta.tck.ws.rs.jaxrs21.ee.client.executor.rx.JAXRSClientIT`
  - `ee.jakarta.tck.ws.rs.jaxrs21.ee.client.rxinvoker.JAXRSClientIT`
