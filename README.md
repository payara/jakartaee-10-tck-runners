# jakartaee-10-tck-runners

# Concurrent 3.0

### Prerequisities

run

    mvn clean dependency:copy
    mvn clean dependency:copy-dependencies`

* it downloads all server dependencies to target/server-dependencies

Copy the files to Payara:

	cp target/server-dependencies/* ${PAYARA}/glassfish/domains/domain1/lib

Prepare password file for account javajoe/javajoe/role=Manager. File`prepare-server-password` contains row:

	AS_ADMIN_USERPASSWORD=javajoe

Go to Payara and start the server

	./asadmin start-domain

Create required resources:

    echo "Setting file user javajoe"
    ./asadmin --passwordfile=prepare-server-password create-file-user --groups=Manager --authrealmname=file javajoe
    echo "Setting system property jimage.dir", needed for SignatureTestServlet
    ./asadmin create-system-properties jimage.dir=/tmp
    echo "Setting allow setAccessible, needed for SignatureTestServlet"
    ./asadmin create-jvm-options "--add-exports=java.base/jdk.internal.vm.annotation=ALL-UNNAMED"
    ./asadmin create-jvm-options "--add-opens=java.base/jdk.internal.vm.annotation=ALL-UNNAMED"
    echo "Setup phase done, stopping Payara"
	./asadmin stop-domain

### Running

Run maven test in the TCK Runner directory

	cd concurrent-tck
    mvn test


## Jakarta JSON Processing

### Prerequisites
To be able to run the TCK test, you need to setup remote Payara Server in order to run the Arquillian against the container

### Test Executions
**(Make sure the Payara server up and running)**

Run maven test from jsonp-tck module
    
    cd jsonp-tck
    mvn test
