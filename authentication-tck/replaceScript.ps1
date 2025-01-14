(Get-Content .\target\authentication-tck-3.1.0\tck\pom.xml).replace('</profiles>', (Get-Content .\payara-profiles.xml)) | Set-Content .\target\authentication-tck-3.1.0\tck\pom.xml

# Remove these properties which activate org.omnifaces:junit-result-listener as it doesn't work with Windows
(Get-Content .\target\authentication-tck-3.1.0\tck\pom.xml).replace('<name>listener</name>', '') | Set-Content .\target\authentication-tck-3.1.0\tck\pom.xml
(Get-Content .\target\authentication-tck-3.1.0\tck\pom.xml).replace('<value>org.omnifaces.junit.ResultListener</value>', '') | Set-Content .\target\authentication-tck-3.1.0\tck\pom.xml


# Not sure if this is the "correct" way to configure this, but this Service Loader is a GlassFish specific class (as in "GlassFish" GlassFish: it's not a part of Metro)
# The class name was changed from what we have in Payara in two commits: https://github.com/eclipse-ee4j/glassfish/commit/0dff8ac0607dbbee94d16d4e7da5c4618b61d295 and https://github.com/eclipse-ee4j/glassfish/commit/5da439185d5d7be1f61fcc201ae3ab53f4fd6a23
Set-Content .\target\authentication-tck-3.1.0\tck\spi\soap\src\main\resources\META-INF\services\com.sun.xml.ws.assembler.metro.dev.ClientPipelineHook "com.sun.enterprise.security.webservices.ClientPipeCreator"
