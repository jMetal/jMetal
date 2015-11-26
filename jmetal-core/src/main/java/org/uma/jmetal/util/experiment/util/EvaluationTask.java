package org.uma.jmetal.util.experiment.util;

/**
 * Created by ajnebro on 11/11/15.
 */

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.experiment.ExperimentConfiguration;
import org.uma.jmetal.util.fileoutput.SolutionSetOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

/** Class defining tasks of an experiment to be computed in parallel */
class EvaluationTask<S extends Solution<?>, Result> implements Callable<Object> {
  private TaggedAlgorithm<Result> algorithm ;
  private int id;
  private ExperimentConfiguration<?, ?> experimentData ;
  private String outputDirectoryName ;

  /** Constructor */
  public EvaluationTask(TaggedAlgorithm<Result> algorithm, int id, ExperimentConfiguration<?,?> experimentData) {
    JMetalLogger.logger.info(
        " Task: " + algorithm.getTag() + ", problem: " + algorithm.getProblem().getName() + ", run: " + id);
    this.algorithm = algorithm ;
    this.id = id;
    this.experimentData = experimentData ;

    outputDirectoryName = experimentData.getExperimentBaseDirectory()
        + "/data/"
        + algorithm.getTag()
        + "/"
        + algorithm.getProblem().getName() ;

    File outputDirectory = new File(outputDirectoryName);
    if (!outputDirectory.exists()) {
      boolean result = new File(outputDirectoryName).mkdirs();
      JMetalLogger.logger.info("Creating " + outputDirectoryName);
    }
  }

  public Integer call() throws Exception {
    String funFile = outputDirectoryName + "/FUN" + id + ".tsv" ;
    String varFile = outputDirectoryName + "/VAR" + id + ".tsv" ;
    JMetalLogger.logger.info(
        " Running algorithm: " + algorithm.getTag() +
            ", problem: " + algorithm.getProblem().getName() +
            ", run: " + id+
            ", funFile: " + funFile);


    algorithm.run();
    Result population = algorithm.getResult() ;

    new SolutionSetOutput((List<? extends Solution<?>>) population)
        .setSeparator("\t")
        .setVarFileOutputContext(new DefaultFileOutputContext(varFile))
        .setFunFileOutputContext(new DefaultFileOutputContext(funFile))
        .print();

    return id;
  }

}
