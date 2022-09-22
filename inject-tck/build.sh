#!/bin/bash

SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"

PORTING=$SCRIPTPATH/ditck-porting
BUNDLES=$PORTING/bundles

if [ ! -d ditck-porting ]; then
   git clone https://github.com/payara/ditck-porting
   cd $PORTING
   git fetch origin
   git checkout EE10
fi

# Cleanup
rm -f $PORTING/latest-glassfish.zip
rm -rf $PORTING/dist/
rm -rf $PORTING/payara6

cd $PORTING

#### Adapted from ditck-porting/docker/build_ditck.sh and ditck-porting/docker/run_ditck.sh ###
export WORKSPACE=$SCRIPTPATH/ditck-porting
mkdir -p ${WORKSPACE}/bundles

if [ ! -f $BUNDLES/jakarta.inject-tck.zip ]; then
  wget https://download.eclipse.org/ee4j/cdi/inject/2.0/jakarta.inject-tck-2.0.2-bin.zip -O $BUNDLES/jakarta.inject-tck.zip
fi
if [ ! -f $BUNDLES/jsr299-tck.zip ]; then
  wget https://download.eclipse.org/ee4j/cdi/4.0/cdi-tck-4.0.4-dist.zip -O $BUNDLES/jsr299-tck.zip
fi

unzip -o ${SCRIPTPATH}/bundles/payara.zip -d ${WORKSPACE}
unzip -o ${BUNDLES}/jakarta.inject-tck.zip -d ${WORKSPACE}
unzip -o ${BUNDLES}/jsr299-tck.zip -d ${WORKSPACE}

sed -i "s#^porting\.home=.*#porting.home=$WORKSPACE#g" "$WORKSPACE/build.xml"
sed -i "s#^glassfish\.home=.*#glassfish.home=$WORKSPACE/payara6/glassfish#g" "$WORKSPACE/build.xml"

ant dist.sani

chmod 777 ${WORKSPACE}/dist/*.zip
cd ${WORKSPACE}/dist/
for entry in `ls 330-tck-*.zip`; do
  date=`echo "$entry" | cut -d_ -f2`
  strippedEntry=`echo "$entry" | cut -d_ -f1`
  echo "copying ${WORKSPACE}/dist/$entry to ${WORKSPACE}/bundles/${strippedEntry}_latest.zip"
  cp ${WORKSPACE}/dist/$entry ${WORKSPACE}/bundles/${strippedEntry}.zip
  chmod 777 ${WORKSPACE}/bundles/${strippedEntry}.zip
done

if ls ${WORKSPACE}/bundles/*330-tck-glassfish-porting-*.zip 1> /dev/null 2>&1; then
  unzip -o ${WORKSPACE}/bundles/*330-tck-glassfish-porting-*.zip -d ${WORKSPACE}
else
  echo "[ERROR] TCK bundle not found"
  exit 1
fi

# Install the porting lib
cd ${WORKSPACE}/cdi-tck-4.0.4/weld/porting-package-lib
mvn --global-settings ${WORKSPACE}/settings.xml clean install
echo "+++ Installed CDI TCK porting libs"
ls target/dependency

#Edit test properties
export TS_HOME=${WORKSPACE}/330-tck-glassfish-porting
export REPORT=${WORKSPACE}/330tck-report
sed -i "s#tck.home=.*#tck.home=${WORKSPACE}/jakarta.inject-tck-2.0.2#g" ${TS_HOME}/build.properties
sed -i "s#porting.home=.*#porting.home=${TS_HOME}#g" ${TS_HOME}/build.properties
sed -i "s#glassfish.home=.*#glassfish.home=${WORKSPACE}/payara6/glassfish#g" ${TS_HOME}/build.properties
sed -i "s#299.tck.home=.*#299.tck.home=${WORKSPACE}/cdi-tck-4.0.4#g" ${TS_HOME}/build.properties
sed -i "s#report.dir=.*#report.dir=${REPORT}#g" ${TS_HOME}/build.properties

#### End of adapted from ditck-porting/docker/build_ditck.sh and ditck-porting/docker/run_ditck.sh ###

cd $SCRIPTPATH