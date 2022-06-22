#!/bin/bash

## This script installs the irace package, sets up the environment and launches
## irace. Then compresses its output as a tar.gz.

#SCENARIO is a irace's scenario file
SCENARIO=$1
# RUN is the run number to distinguish replications of irace
RUN=$2
shift 2
# RUN in condor starts at 0
let SEED=1234567+RUN
IRACE_PARAMS="--scenario scenario-${SCENARIO}.txt --debug-level 3 --parallel 24  --seed ${SEED}"

#tar axf condor-input.tar.gz

RPACKAGE="irace.tar.gz"
# install irace
if [ ! -r $RPACKAGE ]; then
    echo "cannot read $RPACKAGE"
    exit 1
fi
RLIBDIR="$(pwd)/R/"
mkdir -p $RLIBDIR
R CMD INSTALL $RPACKAGE --library=$RLIBDIR
export R_LIBS="$RLIBDIR:$R_LIBS"
irace="$(pwd)/R/irace/bin/irace"
if [ ! -x $irace ]; then
    echo "cannot execute $irace"
    exit 1
fi
export PATH="$(pwd)/":${PATH}
#cat /proc/cpuinfo
#echo "$irace --scenario scenario-${1}.txt --exec-dir=./execdir --debug-level 2 --parallel 8"
mkdir -p execdir && $irace --exec-dir=./execdir ${IRACE_PARAMS} 1> execdir/irace.stdout.txt 2> execdir/irace.stderr.txt
#| xz - > execdir/irace.stdout.xz
#cd ..
#tar acf result.tar.gz irace/execdir
#ls ./execdir
#cat ./execdir/c1-1.stderr
#cd .. && tar acf result.tar.gz irace
#ls ../result.tar.gz
