# Jakarta CDI LangModel TCK

## Prerequisite

execute mvn install to move dependencies to the lib folder and enable assertions

Required property:

`payara.home` : path to payara installation

```
mvn install -Dpayara.home=C:\java\projects\Payara\appserver\distributions\payara\target\stage\payara6 -DskipTests=true
```

## Test Execution

Required property:

`payara.home` : path to payara installation

execute maven test:

```
mvn test -Dpayara.home=C:\java\projects\Payara\appserver\distributions\payara\target\stage\payara6
```
