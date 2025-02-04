# Jakarta Faces TCK Docker Runner

## Prerequisite
Set `JAVA_HOME` to Java 21.

Install the Faces TCK dependencies via tck-download module. Execute from the root directory of TCK runners:

```
mvn clean install -pl .,:tck-download,:jakarta-faces-tck
```

Create the Docker image, specifying whether you intend to test against Platform or Web Profile and your Payara version

```
# Platform
mvn clean install -Dpayara.version=${payara.version} -pl . -pl faces-tck -pl faces-tck-docker-runner

# Web Profile
mvn clean install -Dpayara.version=${payara.version} -pl . -pl faces-tck -pl faces-tck-docker-runner -Pweb
```

## Test Execution

Run the Docker image: `docker container run --name wibbles payara/faces-tck-runner`
NOTE: The Docker image is only intended to be run against the profile it was built for (Platform or Web Profile).

If you wish to log in to the container for whatever reason:
```
# If you want to create a brand new container and log in
docker container run --name wibbles -it payara/faces-tck-runner

# If you want to login to a pre-existing container
docker container start -ia wibbles
```
