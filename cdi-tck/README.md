# How to Run
1. Ensure that `payara.home` is set - Maven will add the "${payara.home}/glassfish" for you so don't include that final bit.
2. Make sure M2_HOME is set.
3. Make sure MAVEN_HOME is set.
4. Make sure ANT_HOME is set.
5. Decide which level of TCK you want to run by defining javaee.level as 'core', or 'full' (default is full).
6. Run `mvn clean verify -Dpayara.home=x -Djavaee.level=y`
