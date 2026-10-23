package org.uma.jmetal.lab.experiment.component.impl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.component.ExperimentComponent;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.impl.ThreadLocalRandomGenerator;

/**
 * This class executes the algorithms the have been configured with a instance of class {@link
 * Experiment}. The algorithms run in parallel, on as many threads as the experiment has cores.
 *
 * <p>The result of the execution is a pair of files FUNrunId.tsv and VARrunID.tsv per
 * org.uma.jmetal.experiment, which are stored in the directory {@link Experiment
 * #getExperimentBaseDirectory()}/algorithmName/problemName.
 *
 * <p>With more than one core, {@link ThreadLocalRandomGenerator#install()} gives each thread its
 * own random number generator before the runs start, so that the threads do not contend for the
 * shared one. If the experiment has a base seed ({@link
 * org.uma.jmetal.lab.experiment.ExperimentBuilder#setSeed(long)}), each run sets the seed of its
 * thread before it starts, so the results are the same whatever the number of cores.
 *
 * @author Antonio J. Nebro
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

    installAGeneratorPerThreadIfParallel();

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
        execute(unfinishedAlgorithmList);
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
    installAGeneratorPerThreadIfParallel();
    execute(experimentAlgorithms);
  }

  /**
   * Runs the algorithms on as many threads as the experiment has cores: in the calling thread with
   * one core, and in a pool of its own otherwise. (Setting the parallelism of the common pool has
   * no effect once the pool has been created, so the runs used to be parallel even with one core.)
   */
  private void execute(List<ExperimentAlgorithm<?, ?>> algorithms) {
    int cores = experiment.getNumberOfCores();
    if (cores <= 1) {
      algorithms.forEach(algorithm -> algorithm.runAlgorithm(experiment));
      return;
    }

    ForkJoinPool pool = new ForkJoinPool(cores);
    try {
      pool.submit(
              () ->
                  algorithms.parallelStream()
                      .forEach(algorithm -> algorithm.runAlgorithm(experiment)))
          .get();
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      throw new JMetalException("The execution of the algorithms was interrupted", exception);
    } catch (ExecutionException exception) {
      throw new JMetalException("The execution of the algorithms failed", exception);
    } finally {
      pool.shutdown();
    }
  }

  private void installAGeneratorPerThreadIfParallel() {
    if (experiment.getNumberOfCores() > 1) {
      ThreadLocalRandomGenerator.install();
    }
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
