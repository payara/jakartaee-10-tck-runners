# jakartaee-10-tck-runners

## Prerequisities

run `mvn clean dependency:copy` and `mvn clean dependency:copy-dependencies`

* it downloads all server dependencies to target/server-dependencies

Copy the files to Payara:

	cp target/server-dependencies/* ${PAYARA}/glassfish/domains/domain1/lib

Start Payara

	./asadmin start-domain

Prepare password file for account javajoe/javajoe/role=Manager. File`prepare-server-password` contains row:

	AS_ADMIN_USERPASSWORD=javajoe

Create required resources:

	./asadmin start-domain
	./asadmin create-managed-executor-service concurrent/ExecutorA
	./asadmin create-managed-executor-service concurrent/ExecutorB
	./asadmin create-managed-executor-service concurrent/ExecutorC
	./asadmin create-managed-scheduled-executor-service concurrent/ScheduledExecutorA
	./asadmin create-managed-scheduled-executor-service concurrent/ScheduledExecutorB
	./asadmin create-managed-scheduled-executor-service concurrent/ScheduledExecutorC
	./asadmin create-managed-scheduled-executor-service concurrent/EJBScheduledExecutorA
	./asadmin create-managed-scheduled-executor-service concurrent/EJBScheduledExecutorB
	./asadmin create-managed-scheduled-executor-service concurrent/EJBScheduledExecutorC
	./asadmin create-managed-executor-service concurrent/EJBExecutorA
	./asadmin create-managed-thread-factory concurrent/EJBThreadFactoryA
	./asadmin create-managed-thread-factory concurrent/EJBThreadFactoryB
	./asadmin create-managed-thread-factory concurrent/ThreadFactoryA
	./asadmin create-managed-thread-factory concurrent/ThreadFactoryB
	./asadmin create-context-service concurrent/ContextA
	./asadmin create-context-service concurrent/ContextB
	./asadmin create-context-service concurrent/ContextC
	./asadmin create-context-service concurrent/EJBContextA
	./asadmin create-context-service concurrent/EJBContextB
	./asadmin create-context-service concurrent/EJBContextC
	./asadmin --passwordfile=prepare-server-password create-file-user --groups=Manager --authrealmname=file javajoe
	./asadmin stop-domain

# Running

Run maven test in the TCK Runner directory

	mvn test
