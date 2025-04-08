# Jakarta CDI Platform TCK Docker Runner

## Prerequisite
- Set `JAVA_HOME` to Java 21.
- Ensure Docker is installed and properly configured.

## Download TCK Dependencies

Use the `tck-download` module to fetch the `jakarta-platform-tck` artifact, which includes all relevant Platform TCKs (including CDI).

Run the following command from the root directory of the TCK runners:

```
mvn clean install -pl .,:tck-download,:jakarta-platform-tck
```

Create the Docker image, specifying whether you intend to test against Platform or Web Profile and your Payara version

```
# Platform
mvn clean install -Dpayara.version=${payara.version} -pl . -pl cdi-platform-tck -pl cdi-platform-tck-docker-runner -DskipTests=true

# Web Profile
mvn clean install -Dpayara.version=${payara.version} -pl . -pl cdi-platform-tck -pl cdi-platform-tck-docker-runner -DskipTests=true -Pweb
```

## Test Execution

The Docker image will be run as a part of the Maven build.

Run the Docker image: `docker container run --name cdi-tck-container --init -it payara/cdi-tck-runner -DskipDockerBuild=true`
NOTE: The Docker image is only intended to be run against the profile it was built for (Platform or Web Profile).

The container will be deleted after the run completes (assuming no prior steps fail).
If you do not wish for this to happen, add `-DskipDockerCleanup=true`

If you wish to log in to the container for whatever reason:
```
# To create a brand new container and log in
docker container run --name cdi-tck-container --entrypoint /bin/bash --init -it  payara/cdi-tck-runner
```
