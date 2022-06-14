#!/bin/bash

if [ ! -d ditck-porting ]; then
   git clone https://github.com/payara/ditck-porting
fi

SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"

echo -----------------------------------------------------------------------
echo Scriptpath: ${SCRIPTPATH}
echo -----------------------------------------------------------------------


PORTING=$SCRIPTPATH/ditck-porting
OUTPUT=$PORTING/bundles

cd $SCRIPTPATH

rm -f $PORTING/latest-glassfish.zip
rm -rf $OUTPUT
rm -rf $OUTPUT/../dist/
rm -rf ditck-porting/payara6

export WORKSPACE=$SCRIPTPATH/ditck-porting

export GF_BUNDLE_URL=http://localhost:8000/payara-prerelease.zip

echo Build should download from $GF_BUNDLE_URL

bash -x $WORKSPACE/docker/build_ditck.sh