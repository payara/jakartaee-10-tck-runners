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
create-system-properties j2eelogin.name=j2ee:j2eelogin.password=j2ee:provider.configuration.file=${tckDir}/spi/common/ProviderConfiguration.xml:vendor.authconfig.factory=com.sun.jaspic.config.factory.AuthConfigFileFactory:log.file.location=${payaraHome}/glassfish/domains/domain1/logs                                    
```

${tckDir} is the directory where the unpacked TCK exists, for example: /home/user/Git/JakartaEE-10-TCK-Runners/authentication-tck/target/authentication-tck-3.1.0/tck

NOTE: On Windows you will need to escape the ':' and '\' characters that will be present on your path variables, due to how the `create-system-properties` parses these characters
For example:
```
create-system-properties "j2eelogin.name=j2ee:j2eelogin.password=j2ee:provider.configuration.file=D\:\\Git\\JakartaEE10-TCK-Runners\\authentication-tck\\target\\authentication-tck-3.1.0\\tck\\spi\\common\\ProviderConfiguration.xml:vendor.authconfig.factory=com.sun.jaspic.config.factory.AuthConfigFileFactory:log.file.location=D\:\\Git\\Payara\\appserver\\distributions\\payara\\target\\stage\\payara7\\glassfish\\domains\\domain1\\logs"
```

Run the TCK with the following command from the module directory, providing the path to the payaraHome and replacing the payara.version property:
```
mvn clean verify -Ppayara-server-managed -Dpayara.home=${payaraHome} -Dpayara.version=${payaraVersion}
```

If on Windows, you may encounter an error where the powershell script cannot be executed due to the local execution policy.
The following will get around this for the terminal session:

```
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```