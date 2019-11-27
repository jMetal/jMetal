package org.uma.jmetal.lab.experiment.lab;

import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Class describing the parameters of an experiment.
 *
 * @author Antonio J. Nebro
 *
 * @param <S>
 * @param <Result>
 */
public class ExperimentSettings<S extends Solution<?>, Result extends List<S>> {
  private final String experimentName;
  private final List<ExperimentAlgorithm<S, Result>> algorithmList;
  private final List<ExperimentProblem<S>> problemList;
  private final String experimentBaseDirectory;

  private final String outputParetoFrontFileName;
  private final String outputParetoSetFileName;

  public String getExperimentName() {
    return experimentName;
  }

  public List<ExperimentAlgorithm<S, Result>> getAlgorithmList() {
    return algorithmList;
  }

  public List<ExperimentProblem<S>> getProblemList() {
    return problemList;
  }

  public String getExperimentBaseDirectory() {
    return experimentBaseDirectory;
  }

  public String getOutputParetoFrontFileName() {
    return outputParetoFrontFileName;
  }

  public String getOutputParetoSetFileName() {
    return outputParetoSetFileName;
  }

  public int getIndependentRuns() {
    return independentRuns;
  }

  private final int independentRuns;

  public ExperimentSettings(
      String experimentName,
      String experimentBaseDirectory,
      List<ExperimentAlgorithm<S, Result>> algorithmList,
      List<ExperimentProblem<S>> problemList,
      String outputParetoFrontFileName,
      String outputParetoSetFileName,
      int independentRuns) {
    this.experimentName = experimentName;
    this.algorithmList = algorithmList;
    this.problemList = problemList;
    this.experimentBaseDirectory = experimentBaseDirectory;
    this.outputParetoFrontFileName = outputParetoFrontFileName;
    this.outputParetoSetFileName = outputParetoSetFileName;
    this.independentRuns = independentRuns;
  }

  public ExperimentSettings(
      String experimentName,
      String experimentBaseDirectory,
      List<ExperimentAlgorithm<S, Result>> algorithmList,
      List<ExperimentProblem<S>> problemList,
      int independentRuns) {
    this(
        experimentName,
        experimentBaseDirectory,
        algorithmList,
        problemList,
        "FUN",
        "VAR",
        independentRuns);
  }
}
