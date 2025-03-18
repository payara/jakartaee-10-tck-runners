# Run commands in order (change env.TS_HOME)
docker run --name james-mail --rm -d -p 1025:1025 -p 1143:1143 --entrypoint=/bin/bash jakartaee/cts-mailserver:0.1 -c /root/startup.sh

mvn clean install -pl . -pl tck-download/ -pl tck-download/jakarta-platform-tck/ -Pjakarta-staging

mvn clean verify -pl . -pl mail-platform-tck/ -Ppayara-server-managed,jakarta-staging -Denv.TS_HOME="/home/kalin/jakartaee-10-tck-runners/mail-platform-tck/jakartaeetck/"