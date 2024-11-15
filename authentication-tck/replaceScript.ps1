(Get-Content .\target\authentication-tck-3.1.0\tck\pom.xml).replace('</profiles>', (Get-Content .\payara-profiles.xml)) | Set-Content .\target\authentication-tck-3.1.0\tck\pom.xml

# Remove these properties which activate org.omnifaces:junit-result-listener as it doesn't work with Windows
(Get-Content .\target\authentication-tck-3.1.0\tck\pom.xml).replace('<name>listener</name>', '') | Set-Content .\target\authentication-tck-3.1.0\tck\pom.xml
(Get-Content .\target\authentication-tck-3.1.0\tck\pom.xml).replace('<value>org.omnifaces.junit.ResultListener</value>', '') | Set-Content .\target\authentication-tck-3.1.0\tck\pom.xml