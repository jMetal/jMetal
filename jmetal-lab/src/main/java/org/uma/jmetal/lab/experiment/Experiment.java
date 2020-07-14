package org.uma.jmetal.lab.experiment;

import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;

import java.util.HashSet;
import java.util.List;

/**
 * Class for describing the configuration an experimental study.
 *
 * @author Antonio J. Nebro
 */
public class Experiment<S extends Solution<?>, Result extends List<S>> {
  private String experimentName;
  private List<ExperimentAlgorithm<S, Result>> algorithmList;
  private List<ExperimentProblem<S>> problemList;
  private String experimentBaseDirectory;

  private String outputParetoFrontFileName;
  private String outputParetoSetFileName;
  private int independentRuns;

  private String referenceFrontDirectory;

  private List<GenericIndicator<S>> indicatorList;

  private int numberOfCores;

  /** Constructor */
  public Experiment(ExperimentBuilder<S, Result> builder) {
    this.experimentName = builder.getExperimentName();
    this.experimentBaseDirectory = builder.getExperimentBaseDirectory();
    this.algorithmList = builder.getAlgorithmList();
    this.problemList = builder.getProblemList();
    this.independentRuns = builder.getIndependentRuns();
    this.outputParetoFrontFileName = builder.getOutputParetoFrontFileName();
    this.outputParetoSetFileName = builder.getOutputParetoSetFileName();
    this.numberOfCores = builder.getNumberOfCores();
    this.referenceFrontDirectory = builder.getReferenceFrontDirectory();
    this.indicatorList = builder.getIndicatorList();
  }

  /* Getters */
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

  public int getNumberOfCores() {
    return numberOfCores;
  }

  public String getReferenceFrontDirectory() {
    return referenceFrontDirectory;
  }

  public List<GenericIndicator<S>> getIndicatorList() {
    return indicatorList;
  }

  /* Setters */
  public void setReferenceFrontDirectory(String referenceFrontDirectory) {
    this.referenceFrontDirectory = referenceFrontDirectory;
  }

  public void setAlgorithmList(List<ExperimentAlgorithm<S, Result>> algorithmList) {
    this.algorithmList = algorithmList;
  }

  /**
   * The list of algorithms contain an algorithm instance per problem. This is not convenient for
   * calculating statistical data, because a same algorithm will appear many times. This method
   * remove duplicated algorithms and leave only an instance of each one.
   */
  public void removeDuplicatedAlgorithms() {
    HashSet<String> algorithmTagList = new HashSet<>();

    getAlgorithmList().removeIf(alg -> !algorithmTagList.add(alg.getAlgorithmTag()));
  }
}
