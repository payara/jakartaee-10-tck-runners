# jakartaee-10-tck-runners

Payara runners for the standalone Jakarta EE TCKs.
See individual module READMEs for details, but general usage is:

`mvn clean verify -Dpayara.version=${payaraVersion} -Ppayara-server-managed -pl . -pl tck-download -pl tck-download/${tckToRun} -pl ${tckToRun}`