#!/bin/bash

## This script installs the irace package, sets up the environment and launches
## irace. Then compresses its output as a tar.gz.

#SCENARIO is a irace's scenario file
SCENARIO=$1
# RUN is the run number to distinguish replications of irace
RUN=$2
shift 2
N_CPUS=${SLURM_CPUS_PER_TASK:-4}
let SEED=1234567+RUN
EXECDIR=$(dirname ${SCENARIO})/execdir-${RUN}
IRACE_PARAMS="--scenario ${SCENARIO} --debug-level 1 --parallel $N_CPUS --seed ${SEED} --exec-dir=${EXECDIR}"

RPACKAGE="./irace_3.5.tar.gz"
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
echo "$irace ${IRACE_PARAMS} 1> ${EXECDIR}/irace.stdout.txt 2> ${EXECDIR}/irace.stderr.txt"
mkdir -p $EXECDIR \
    && ln -fs $(pwd)/resources ${EXECDIR}/ \
    && $irace ${IRACE_PARAMS} 1> ${EXECDIR}/irace.stdout.txt 2> ${EXECDIR}/irace.stderr.txt
#| xz - > execdir/irace.stdout.xz
#cd ..
#tar acf result.tar.gz irace/execdir
#ls ./execdir
#cat ./execdir/c1-1.stderr
#cd .. && tar acf result.tar.gz irace
#ls ../result.tar.gz
