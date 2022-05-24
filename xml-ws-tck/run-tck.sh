#!/bin/bash
export PAYARA=$1
export PAYARA_REPL=${PAYARA//\//\\/}

if [ JAVA_HOME = "" ] ; then
    export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
fi
export TS_HOME=`pwd`/xml-ws-tck
export PATH=${PATH}:${TS_HOME}/bin

rm jakarta-xml-ws-tck-4.0.0.zip
wget https://download.eclipse.org/jakartaee/xml-web-services/4.0/jakarta-xml-ws-tck-4.0.0.zip
rm -rf ./xml-ws-tck
unzip jakarta-xml-ws-tck-4.0.0.zip


cp prepare-server-password ${PAYARA}/bin
pushd .
cd ${PAYARA}/bin
echo "Starting Payara for setup"
./asadmin start-domain
echo "Setting file user javajoe"
./asadmin --passwordfile=prepare-server-password create-file-user --groups=Manager --authrealmname=file javajoe
echo "Setting system property jimage.dir", needed for SignatureTestServlet
./asadmin create-system-properties jimage.dir=/tmp
echo "Setting allow setAccessible, needed for SignatureTestServlet"
./asadmin create-jvm-options "--add-exports=java.base/jdk.internal.vm.annotation=ALL-UNNAMED"
./asadmin create-jvm-options "--add-opens=java.base/jdk.internal.vm.annotation=ALL-UNNAMED"
echo "Setup phase done, stopping Payara"
./asadmin stop-domain
popd


# replace settings
if [[ ! -f ${TS_HOME}/bin/ts.jte-origin ]]
then
   mv ${TS_HOME}/bin/ts.jte ${TS_HOME}/bin/ts.jte-origin
fi
sed "s/^webcontainer.home=\$/webcontainer.home=${PAYARA_REPL}glassfish/" ${TS_HOME}/bin/ts.jte-origin > ${TS_HOME}/bin/ts.jte-temp1

mv ${TS_HOME}/bin/ts.jte-temp1 ${TS_HOME}/bin/ts.jte

cd ${TS_HOME}/bin
ant config.vi

ant deploy.all

ant gui
