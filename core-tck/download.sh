#!/bin/bash -x

SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"
BUNDLES=${SCRIPTPATH}/bundles

if [ ! -f ${BUNDLES}/jakarta-core-profile-tck-10.0.0.zip ]; then
  wget https://download.eclipse.org/jakartaee/coreprofile/10.0/jakarta-core-profile-tck-10.0.0.zip -O $BUNDLES/jakarta-core-profile-tck-10.0.0.zip
fi