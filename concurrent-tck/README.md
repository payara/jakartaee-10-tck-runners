# Concurrent 3.0

### Prerequisities

run

    mvn clean dependency:copy
    mvn clean dependency:copy-dependencies

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

Temporal fix for unknown tag 18: copy default-web.xml to domains/domain1/config
This sets suppressSmap to false, e.g. smap is not generated. Once Payara will use newer Tomcat, it will be fixed
and this temporal fix can be removed.

### Running

Run maven test in the TCK Runner directory

    mvn test

For running against Payara Web Profile, activate the `web` profile

    mvn test -Pweb