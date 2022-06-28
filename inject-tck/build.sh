#!/bin/bash

if [ ! -f jakarta.inject-tck-2.0.1-bin.zip ]; then
  wget https://download.eclipse.org/ee4j/cdi/inject/2.0/jakarta.inject-tck-2.0.1-bin.zip -O jakarta.inject-tck-2.0.1-bin.zip
fi

SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"

echo -----------------------------------------------------------------------
echo Scriptpath: ${SCRIPTPATH}
echo -----------------------------------------------------------------------

PORTING=$SCRIPTPATH/ditck-porting
OUTPUT=$PORTING/bundles

if [ ! -d ditck-porting ]; then
   git clone https://github.com/payara/ditck-porting
   cd $PORTING
   git fetch origin
   git checkout EE10
fi

cd $SCRIPTPATH

rm -f $PORTING/latest-glassfish.zip
rm -rf $OUTPUT
rm -rf $OUTPUT/../dist/
rm -rf ditck-porting/payara6

export WORKSPACE=$SCRIPTPATH/ditck-porting

bash -x $WORKSPACE/docker/build_ditck.sh