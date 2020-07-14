package org.uma.jmetal.lab.experiment.component.impl;

import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.component.ExperimentComponent;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * This class executes the algorithms the have been configured with a instance of class {@link
 * Experiment}. Java 8 parallel streams are used to run the algorithms in parallel.
 *
 * <p>The result of the execution is a pair of files FUNrunId.tsv and VARrunID.tsv per
 * org.uma.jmetal.experiment, which are stored in the directory {@link Experiment
 * #getExperimentBaseDirectory()}/algorithmName/problemName.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ExecuteAlgorithms<S extends Solution<?>, Result extends List<S>>
    implements ExperimentComponent {
  private Experiment<S, Result> experiment;

  /** Constructor */
  public ExecuteAlgorithms(Experiment<S, Result> configuration) {
    this.experiment = configuration;
  }

  @Override
  public void run() {
    JMetalLogger.logger.info("ExecuteAlgorithms: Preparing output directory");
    prepareOutputDirectory();

    System.setProperty(
        "java.util.concurrent.ForkJoinPool.common.parallelism",
        "" + this.experiment.getNumberOfCores());

    int retryCounter = 0 ;
    int maxRetries = 5 ;
    boolean computationNotFinished = true ;

    while (computationNotFinished && (retryCounter < maxRetries)) {
      List<ExperimentAlgorithm<?, ?>> unfinishedAlgorithmList = checkTaskStatus() ;
      if (unfinishedAlgorithmList.size() == 0) {
        computationNotFinished = false ;
      } else {
        JMetalLogger.logger.info(
            "ExecuteAlgorithms: there are " + unfinishedAlgorithmList.size() + " runs pending");
        unfinishedAlgorithmList
            .parallelStream()
            .forEach(algorithm -> algorithm.runAlgorithm(experiment));
        retryCounter++;
      }
    }

    if (computationNotFinished) {
      JMetalLogger.logger.severe("There are unfinished tasks after " + maxRetries + " tries");
    } else {
      JMetalLogger.logger.info("Algorithm runs finished. Number of tries: " + retryCounter);
    }
  }

  public List<ExperimentAlgorithm<?, ?>> checkTaskStatus() {
    List<ExperimentAlgorithm<?, ?>> unfinishedAlgorithmList = new LinkedList<>();

    for (ExperimentAlgorithm<?, ?> algorithm : experiment.getAlgorithmList()) {
      String resultFileName =
          experiment.getExperimentBaseDirectory()
              + "/data/"
              + algorithm.getAlgorithmTag()
              + "/"
              + algorithm.getProblemTag()
              + "/" + experiment.getOutputParetoFrontFileName()
              + algorithm.getRunId()
              + ".csv";
      File file = new File(resultFileName);
      if (!file.exists()) {
        unfinishedAlgorithmList.add(algorithm);
        System.out.println(resultFileName + ". Status: " + file.exists());
      }
    }
    return unfinishedAlgorithmList;
  }

  public void runMissingExecutions(List<ExperimentAlgorithm<?, ?>> experimentAlgorithms) {
    experimentAlgorithms.parallelStream().forEach(algorithm -> algorithm.runAlgorithm(experiment));
  }

  private void prepareOutputDirectory() {
    if (experimentDirectoryDoesNotExist()) {
      createExperimentDirectory();
    }
  }

  private boolean experimentDirectoryDoesNotExist() {
    boolean result;
    File experimentDirectory;

    experimentDirectory = new File(experiment.getExperimentBaseDirectory());
    result = !experimentDirectory.exists() || !experimentDirectory.isDirectory();

    return result;
  }

  private void createExperimentDirectory() {
    File experimentDirectory;
    experimentDirectory = new File(experiment.getExperimentBaseDirectory());

    if (experimentDirectory.exists()) {
      experimentDirectory.delete();
    }

    boolean result;
    result = new File(experiment.getExperimentBaseDirectory()).mkdirs();
    if (!result) {
      throw new JMetalException(
          "Error creating org.uma.jmetal.experiment directory: "
              + experiment.getExperimentBaseDirectory());
    }
  }
}
