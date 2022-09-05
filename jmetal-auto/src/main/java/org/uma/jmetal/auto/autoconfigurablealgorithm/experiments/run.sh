
for variables in 2048 4096 8192 16384 32768 65356 131072
do
  for problem in ZDT1 ZDT2 ZDT3 ZDT6
  do
    for algorithm in NSGAII AUTONSGAII
    do
      java -cp jmetal-auto-6.0-SNAPSHOT-jar-with-dependencies.jar org.uma.jmetal.auto.autoconfigurablealgorithm.experiments.ZDTSLargeScaleStudyV2 $variables $problem $algorithm &
    done
  done
done

