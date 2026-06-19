# Run commands in order (change env.TS_HOME)
mvn clean install -pl . -pl tck-download/ -pl tck-download/jakarta-platform-tck/

mvn clean verify -pl . -pl mail-platform-tck/ -Ppayara-server-managed