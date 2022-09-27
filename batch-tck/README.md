<!--- 
Copyright (c) 2022 Contributors to the Eclipse Foundation

See the NOTICE file distributed with this work for additional information regarding copyright 
ownership. Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. You may 
obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
Unless required by applicable law or agreed to in writing, software distributed 
under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES 
OR CONDITIONS OF ANY KIND, either express or implied. See the License for 
the specific language governing permissions and limitations under the License. 
SPDX-License-Identifier: Apache-2.0
--->

# Executing Jakarta Batch TCK against Payara

To run the full TCK against Payara Server using the managed Arquillian container, first edit the pom and define your payara.version.
You **CANNOT** simply override this with `-Dpayara.version` - Shrinkwrap will ignore this and **ALWAYS** read the pom as-is.

Once you have defined your payara version, execute:

```
mvn clean verify
```

To run the full TCK against Payara Server using remote Arquillian container, activate the `payara-server-remote` profile and specify `payara.home`:

```
mvn clean verify -Dpayara.home=/path/to/payara6 -Ppayara-server-remote
```

If you wish to skip the test setup (for example if re-running against a remote server), add the `-Dskip.setup` property:

```
mvn clean verify -Dpayara.home=/path/to/payara6 -Dskip.setup -Ppayara-server-remote
```

To test against Payara Web (managed container), activate the `-Ppayara-web` profile:

```
mvn clean verify -Ppayara-web
```

Alternatively, you can define the `-Dpayara.artifact` property yourself:

```
mvn clean verify -Dpayara.artifact=payara-web
```

## Details

This module is composed of 2 modules, which are executed from the root module:

* apitests - executes API behavior tests against a running Payara server
* sigtest - executes API signature tests against Payara classes