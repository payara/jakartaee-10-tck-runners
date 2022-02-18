# jakartaee-10-tck-runners

## Prerequisities

run `mvn clean dependency:copy` and `mvn clean dependency:copy-dependencies`

* it downloads all server dependencies to target/server-dependencies

Copy the files to Payara:

	cp target/server-dependencies/* ${PAYARA}/glassfish/domains/domain1/lib

Start Payara

	./asadmin start-domain

Create required resources:

	./asadmin create-managed-executor-service concurrent/ExecutorA
	./asadmin create-managed-executor-service concurrent/ExecutorB
	./asadmin create-managed-executor-service concurrent/ExecutorC
	./asadmin create-managed-scheduled-executor-service concurrent/ScheduledExecutorA
	./asadmin create-managed-scheduled-executor-service concurrent/ScheduledExecutorB
	./asadmin create-managed-scheduled-executor-service concurrent/ScheduledExecutorC
