#!/bin/bash -x

SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"
BUNDLES=${SCRIPTPATH}/bundles

if [ ! -f ${BUNDLES}/jakarta-core-profile-tck-10.0.0.zip ]; then
  wget https://download.eclipse.org/ee4j/jakartaee-tck/jakartaee10/staged/eftl/jakarta-core-profile-tck-10.0.0.zip -O $BUNDLES/jakarta-core-profile-tck-10.0.0.zip
fi