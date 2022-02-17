# jakartaee-10-tck-runners

## Prerequisities

run `mvn clean dependency:copy`
* it downloads all server dependencies to target/server-dependencies

Copy the files to Payara:

	cp target/server-dependencies/* ${PAYARA}/glassfish/domains/domain1/lib
