# Jakarta Core Profile TCK

## Prerequisite
To be able to download and install the Core Profile TCK dependencies, since they're not published to Maven

1. Run the `download.sh` script
2. Run the `install.sh` script

If on Windows:

1. Download https://download.eclipse.org/ee4j/jakartaee-tck/jakartaee10/staged/eftl/jakarta-core-profile-tck-10.0.0.zip
2. Unzip it
3. Install the dependencies with `mvn install -f core-profile-tck-10.0.0\artifacts\pom.xml`

## Test Execution

Required property to be able to execute test

`payara.home` : path to payara installation

Execute maven test with verify:

`mvn verify -Dpayara.home=[path to payara installation]`