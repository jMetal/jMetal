package org.uma.jmetal.experiment.result;

import org.uma.jmetal.experiment.ExperimentData;
import org.uma.jmetal.experiment.ExperimentResult;
import org.uma.jmetal.util.Configuration;

/**
 * Created by Antonio J. Nebro on 18/07/14.
 */
public class ExperimentExecution implements ExperimentResult {

  private MultithreadedExperimentExecutor parallelExecutor ;
  private String paretoSetFileName ;
  private String paretoFrontFileName ;
  private boolean useAlgorithmConfigurationFiles ;

  /** Constructor */
  private ExperimentExecution(Builder builder) {
    parallelExecutor = builder.parallelExecutor ;

    paretoFrontFileName = builder.paretoFrontFileName ;
    paretoSetFileName = builder.paretoSetFileName ;
    useAlgorithmConfigurationFiles = builder.useAlgorithmConfigurationFiles ;
  }

  /* Getters */
  public String getParetoSetFileName() {
    return paretoSetFileName;
  }

  public String getParetoFrontFileName() {
    return paretoFrontFileName;
  }

  public boolean useAlgorithmConfigurationFiles() {
    return useAlgorithmConfigurationFiles;
  }

  /** Builder class */
  public static class Builder {
    MultithreadedExperimentExecutor parallelExecutor ;
    int numberOfThreads ;
    private String paretoSetFileName ;
    private String paretoFrontFileName ;
    private boolean useAlgorithmConfigurationFiles ;

    /** Builder class */
    public Builder(int numberOfThreads) {
    this.numberOfThreads = numberOfThreads ;
      parallelExecutor = new MultithreadedExperimentExecutor(numberOfThreads) ;
      paretoFrontFileName = "FUN" ;
      paretoSetFileName = "VAR" ;
      useAlgorithmConfigurationFiles = false ;
    }

    public Builder getParetoFrontFileName(String fileName) {
      paretoFrontFileName = fileName ;

      return this ;
    }

    public Builder getParetoSetFileName(String fileName) {
      paretoSetFileName = fileName ;

      return this ;
    }

    public Builder useAlgorithmConfigurationFiles() {
      useAlgorithmConfigurationFiles = true ;

      return this ;
    }

    public ExperimentExecution build() {
      return new ExperimentExecution(this) ;
    }
  }

  @Override
  public void generate(ExperimentData experimentData) {
    parallelExecutor.start(this);

    for (String algorithm : experimentData.getAlgorithmNameList()) {
      for (String problem : experimentData.getProblemList()) {
        for (int i = 0; i < experimentData.getIndependentRuns(); i++) {
          Configuration.logger.info(
            "Adding task. Algorithm:  " + algorithm + " Problem: " + problem + " Run: " + i);
          parallelExecutor.addTask(new Object[] {algorithm, problem, i, experimentData});
        }
      }
    }

    parallelExecutor.parallelExecution();
    parallelExecutor.stop();
  }
}
