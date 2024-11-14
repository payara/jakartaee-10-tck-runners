# Jakarta Authentication TCK Runner

## Prerequisite

Download and install the TCK into your local Maven repo.
From the top-level directory: `mvn clean install -pl . -pl tck-download -pl tck-download/jakarta-authentication-tck -Pjakarta-staging`

## Test Execution

To execute the full TCK against a managed Payara Server, run from the module directory

```
mvn clean verify -Ppayara-server-managed
```

If on Windows, you may encounter an error where the powershell script cannot be executed due to the local execution policy.
The following will get around this for the terminal session:

```
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```
