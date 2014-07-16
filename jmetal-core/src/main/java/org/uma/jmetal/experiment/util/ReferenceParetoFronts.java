package org.uma.jmetal.experiment.util;

import org.uma.jmetal.experiment.Experiment;
import org.uma.jmetal.qualityIndicator.util.MetricsUtil;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.NonDominatedSolutionList;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Created by Antonio J. Nebro on 23/02/14.
 */

public class ReferenceParetoFronts implements IExperimentOutput {
  public Experiment experiment;

  public ReferenceParetoFronts(Experiment experiment) {
    this.experiment = experiment;
  }

  @Override
  public void generate() {
    for (int i = 0; i < experiment.getProblemList().length; i++) {
      generateReferenceFronts(i);
    }
  }

  /**
   * @param problemIndex
   */
  private void generateReferenceFronts(int problemIndex) {

    File rfDirectory;
    String referenceFrontDirectory = experiment.getExperimentBaseDirectory() + "/referenceFronts";

    rfDirectory = new File(referenceFrontDirectory);

    if (!rfDirectory.exists()) {
      boolean result = new File(referenceFrontDirectory).mkdirs();
      Configuration.logger.info("Creating " + referenceFrontDirectory);
    }

    String referenceParetoFront =
      referenceFrontDirectory + "/" + experiment.getProblemList()[problemIndex] + ".pf";

    MetricsUtil metricsUtils = new MetricsUtil();
    NonDominatedSolutionList solutionSet = new NonDominatedSolutionList();
    for (String anAlgorithmNameList_ : experiment.getAlgorithmNameList()) {

      String problemDirectory =
        experiment.getExperimentBaseDirectory() + "/data/" + anAlgorithmNameList_ +
          "/" + experiment.getProblemList()[problemIndex];

      for (int numRun = 0; numRun < experiment.getIndependentRuns(); numRun++) {

        String outputParetoFrontFilePath;
        outputParetoFrontFilePath = problemDirectory + "/FUN." + numRun;
        String solutionFrontFile = outputParetoFrontFilePath;

        metricsUtils.readNonDominatedSolutionSet(solutionFrontFile, solutionSet);
      }
    }
    try {
      solutionSet.printObjectivesToFile(referenceParetoFront);
    } catch (IOException e) {
      Configuration.logger.log(Level.SEVERE, "Error", e);
    }
  }
}
