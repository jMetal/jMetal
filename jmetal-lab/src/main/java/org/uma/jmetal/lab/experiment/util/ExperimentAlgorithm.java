package org.uma.jmetal.lab.experiment.util;

import java.io.File;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class defining tasks for the execution of algorithms in parallel.
 *
 * @author Antonio J. Nebro
 */
public class ExperimentAlgorithm<S extends Solution<?>, Result extends List<S>> {
  private Algorithm<Result> algorithm;
  private String algorithmTag;
  private String problemTag;
  private int runId;

  /** Constructor */
  public ExperimentAlgorithm(
      Algorithm<Result> algorithm, String algorithmTag, ExperimentProblem<S> problem, int runId) {
    this.algorithm = algorithm;
    this.algorithmTag = algorithmTag;
    this.problemTag = problem.getTag();
    this.runId = runId;
  }

  public ExperimentAlgorithm(Algorithm<Result> algorithm, ExperimentProblem<S> problem, int runId) {

    this(algorithm, algorithm.name(), problem, runId);
  }

  public void runAlgorithm(Experiment<?, ?> experimentData) {
    String outputDirectoryName =
        experimentData.getExperimentBaseDirectory() + "/data/" + algorithmTag + "/" + problemTag;

    File outputDirectory = new File(outputDirectoryName);
    if (!outputDirectory.exists()) {
      boolean result = new File(outputDirectoryName).mkdirs();
      if (result) {
        JMetalLogger.logger.info("Creating " + outputDirectoryName);
      } else {
        JMetalLogger.logger.severe("Creating " + outputDirectoryName + " failed");
      }
    }

    String funFile =
        outputDirectoryName + "/" + experimentData.getOutputParetoFrontFileName() + runId + ".csv";
    String varFile =
        outputDirectoryName + "/" + experimentData.getOutputParetoSetFileName() + runId + ".csv";
    String message = " Running algorithm: "
        + algorithmTag
        + ", problem: "
        + problemTag
        + ", run: "
        + runId
        + ", funFile: "
        + funFile ;
    JMetalLogger.logger.info(message);

    experimentData
        .getSeed()
        .ifPresent(baseSeed -> JMetalRandom.getInstance().setSeed(seedOfTheRun(baseSeed)));

    try {
      algorithm.run();
      Result population = algorithm.result();

      new SolutionListOutput(population)
          .setVarFileOutputContext(new DefaultFileOutputContext(varFile, ","))
          .setFunFileOutputContext(new DefaultFileOutputContext(funFile, ","))
          .print();
    } catch (Exception exception) {
      JMetalLogger.logger.warning("Execution failed: " + funFile + " has not been created.");
    }
  }

  /**
   * The seed of this run in an experiment with a base seed: the base seed, the tags of the algorithm
   * and the problem and the number of the run, mixed with SplitMix64, so that it does not depend on
   * the order or the thread in which the runs are executed.
   */
  public long seedOfTheRun(long baseSeed) {
    long seed = mix(baseSeed + algorithmTag.hashCode());
    seed = mix(seed + problemTag.hashCode());
    return mix(seed + runId);
  }

  private static long mix(long value) {
    long z = value + 0x9E3779B97F4A7C15L;
    z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
    z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
    return z ^ (z >>> 31);
  }

  public Algorithm<Result> getAlgorithm() {
    return algorithm;
  }

  public String getAlgorithmTag() {
    return algorithmTag;
  }

  public String getProblemTag() {
    return problemTag;
  }

  public int getRunId() {
    return this.runId;
  }
}
