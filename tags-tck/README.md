# How to run
1. Run `mvn install` in `tck-download/jakarta-tags-tck`
2. Ensure Payara is installed in local maven repository.
3. Run `mvn clean verify -B -Dpayara.version=7.2024.1.Alpha2-SNAPSHOT -Ppayara-server-managed`