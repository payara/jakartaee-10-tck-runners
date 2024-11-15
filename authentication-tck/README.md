# Jakarta Authentication TCK Runner

## Prerequisite

Download and install the TCK into your local Maven repo.
From the top-level directory: `mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-authentication-tck -Pjakarta-staging`

## Test Execution

### Managed Server Profile
NOTE: The payara-server-managed doesn't work on Windows. 
The `create-system-properties` asadmin command doesn't play well with Windows.

To execute the full TCK against a managed Payara Server, run from the module directory, replacing the payara.version property.

```
mvn clean verify -Ppayara-server-managed -Dpayara.version=${payaraVersion}
```

### Remote Server Profile

To execute the full TCK against a remote Payara Server installation, start your Payara Server and run the follow asadmin command against it, replacing ${tckDir} and ${payaraHome}:

```
create-system-properties j2eelogin.name=j2ee:j2eelogin.password=j2ee:provider.configuration.file=${tckDir}/spi/common/ProviderConfiguration.xml:vendor.authconfig.factory=org.glassfish.epicyro.config.factory.file.AuthConfigFileFactory:log.file.location=${payaraHome}/glassfish/domains/domain1/logs                                    
```

NOTE: On Windows you will need to escape the ':' and '\' characters that will be present on your path variables, due to how the `create-system-properties` parses these characters

Run the TCK with the following command from the module directory, providing the path to the payaraHome and replacing the payara.version property:
```
mvn clean verify -Ppayara-server-managed -Dpayara.home=${payaraHome} -Dpayara.version=${payaraVersion}
```

If on Windows, you may encounter an error where the powershell script cannot be executed due to the local execution policy.
The following will get around this for the terminal session:

```
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```