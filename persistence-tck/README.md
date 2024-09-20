# Jakarta Persistence TCK

## Usage
1. In `tck-download\tck-persistence` run `mvn clean install` to download the TCK.
2. In `tck-download\tck-persistence\artifacts` run `artifact-install.sh` to install the TCK dependencies.
3. In `persistence-tck` run `mvn clean verify -Ppayara-server-managed`