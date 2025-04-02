# Run commands in order (change env.TS_HOME)
mvn clean install -pl . -pl tck-download/ -pl tck-download/jakarta-platform-tck/ -Pjakarta-staging

mvn clean verify -pl . -pl cdi-platform-tck/ -Dpayara.version=7.2025.1.Alpha1-SNAPSHOT -Dpayara.home=../appserver/distributions/payara/target/stage/payara7 -Ppayara-server-managed,jakarta-staging