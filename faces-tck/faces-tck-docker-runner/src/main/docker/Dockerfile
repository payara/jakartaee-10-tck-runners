FROM eclipsecbijenkins/basic-ubuntu-chrome

EXPOSE 4848 5005 8080 8181 9009 14848 18080 18181

WORKDIR /opt

# Download and install Java, Maven, and Ant
ADD https://cdn.azul.com/zulu/bin/zulu21.40.17-ca-jdk21.0.6-linux_x64.zip https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip https://dlcdn.apache.org//ant/binaries/apache-ant-1.10.15-bin.zip /opt/
RUN unzip zulu21.40.17-ca-jdk21.0.6-linux_x64.zip \
    && rm zulu21.40.17-ca-jdk21.0.6-linux_x64.zip \
    && mv zulu21.40.17-ca-jdk21.0.6-linux_x64 /opt/java/zulu \
    && unzip apache-maven-3.9.9-bin.zip \
    && rm apache-maven-3.9.9-bin.zip \
    && mv apache-maven-3.9.9 /opt/maven \
    && unzip apache-ant-1.10.15-bin.zip \
    && rm apache-ant-1.10.15-bin.zip \
    && mv apache-ant-1.10.15 /opt/ant
ENV JAVA_HOME=/opt/java/zulu MAVEN_HOME=/opt/maven ANT_HOME=/opt/ant PATH=/opt/java/zulu/bin:/opt/maven/bin:/opt/ant/bin:${PATH}

# Copy the Faces TCK and Payara distribution we're testing
COPY maven/faces-tck /opt/faces-tck
COPY maven/payara /opt/payara

# Install Payara in the Docker's local maven repository so it can be resolved
RUN mvn install:install-file -DgroupId=fish.payara.distributions -DartifactId=@payara.artifact@ -Dpackaging=zip -Dversion=@payara.version@ -Dfile=/opt/payara/@payara.artifact@-@payara.version@.zip

CMD ["mvn", "help:active-profiles", "clean", "verify", "-f", "/opt/faces-tck/tck/pom.xml", "-Ppayara-ci-managed,!glassfish-ci-managed@additionalProfiles@"]
