#!/bin/bash
SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"

export PORTING=$SCRIPTPATH/ditck-porting
export WORKSPACE=$SCRIPTPATH/ditck-porting

### Adapted from run_ditck.sh ###
export TS_HOME=${WORKSPACE}/330-tck-glassfish-porting

which ant
ant -version

REPORT=${WORKSPACE}/330tck-report
mkdir -p ${REPORT}

#Run Tests
cd ${TS_HOME}
echo "+++ Ant build.properties:"
cat build.properties
ant run

#Generate Reports
cp ${REPORT}/index.html  ${REPORT}/report.html
echo "Saving TCK results"

mv $REPORT/TESTS-TestSuites.xml $REPORT/330tck-junit-report.xml
rm $REPORT/TEST-*.xml

tar zcvf ${WORKSPACE}/330tck-results.tar.gz ${REPORT}
### End of adapted run_ditck.sh ###

cd ${SCRIPTPATH}