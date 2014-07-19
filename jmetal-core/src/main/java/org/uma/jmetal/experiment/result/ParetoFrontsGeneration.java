package org.uma.jmetal.experiment.result;

import org.uma.jmetal.experiment.ExperimentData;
import org.uma.jmetal.experiment.ExperimentResult;
import org.uma.jmetal.qualityIndicator.util.MetricsUtil;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.NonDominatedSolutionList;
import org.uma.jmetal.util.fileOutput.SolutionSetOutput;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Created by Antonio J. Nebro on 19/07/14.
 */
public class ParetoFrontsGeneration implements ExperimentResult{
  private ExperimentData experimentData ;

  /** Constructor */
  private ParetoFrontsGeneration(Builder builder) {
    this.experimentData = builder.experimentData ;
  }

  /** Builder class */
  public static class Builder {
    private ExperimentData experimentData ;

    public Builder(ExperimentData experimentData) {
      this.experimentData = experimentData ;
    }

    public ParetoFrontsGeneration build() {
      return new ParetoFrontsGeneration(this) ;
    }
  }

  @Override
  public void generate() {
    for (int i = 0; i < experimentData.getProblemList().length; i++) {
      generateReferenceFront(i);
    }
  }

  private void generateReferenceFront(int problemIndex) {

    File rfDirectory;
    String referenceFrontDirectory = experimentData.getExperimentBaseDirectory() + "/referenceFronts";

    rfDirectory = new File(referenceFrontDirectory);

    if (!rfDirectory.exists()) {
      boolean result = new File(referenceFrontDirectory).mkdirs();
      if (!result) {
        throw new JMetalException("Error creating reference Pareto front directory: " + referenceFrontDirectory) ;
      } else {
        Configuration.logger.info("Creating " + referenceFrontDirectory);
      }
    }

    String referenceParetoFrontFile =
      referenceFrontDirectory + "/" + experimentData.getProblemList()[problemIndex] + ".pf";

    MetricsUtil metricsUtils = new MetricsUtil();
    NonDominatedSolutionList solutionSet = new NonDominatedSolutionList();
    for (String algorithmName : experimentData.getAlgorithmNameList()) {

      String problemDirectory =
        experimentData.getExperimentBaseDirectory() + "/data/" + algorithmName +
          "/" + experimentData.getProblemList()[problemIndex];

      for (int numRun = 0; numRun < experimentData.getIndependentRuns(); numRun++) {

        String outputParetoFrontFilePath;
        outputParetoFrontFilePath = problemDirectory + "/FUN." + numRun;
        String solutionFrontFile = outputParetoFrontFilePath;

        metricsUtils.readNonDominatedSolutionSet(solutionFrontFile, solutionSet);
      }
    }
    try {
      SolutionSetOutput.printObjectivesToFile(solutionSet, referenceParetoFrontFile);
    } catch (IOException e) {
      Configuration.logger.log(Level.SEVERE, "Error", e);
    }
  }
}
