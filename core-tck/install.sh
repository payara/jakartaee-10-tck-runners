#!/bin/bash -x

SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"
BUNDLES=${SCRIPTPATH}/bundles

if [ ! -d ${BUNDLES} ]; then
   echo "bundles directory appears to be missing: panic and exit!"
   exit 1
fi

if [ ! -f ${BUNDLES}/jakarta-core-profile-tck-10.0.0.zip ]; then
  echo "10.0.0 TCK directory appears to be missing: panic and exit!"
  exit 1
fi

cd ${BUNDLES}
unzip jakarta-core-profile-tck-10.0.0.zip -d .

cd core-profile-tck-10.0.0/artifacts
mvn install

cd ${SCRIPTPATH}
