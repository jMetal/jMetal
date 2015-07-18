package org.uma.jmetal.runner.multiobjective;

import org.uma.jmetal.algorithm.Algorithm;

/**
 * Created by ajnebro on 17/7/15.
 */
public abstract class AbstractRunner {
  public static void printFinalSolutionSet(Algorithm<?> algorithm) {
    /*
    List<Solution<?>> population = (List<Solution<?>>) algorithm.getResult();

    new SolutionSetOutput.Printer(population)
        .setSeparator("\t")
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();

    //JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
    */
  }
}
