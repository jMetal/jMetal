package jmetal.experiments.util;

import jmetal.experiments.Experiment;
import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.Configuration;
import jmetal.util.NonDominatedSolutionList;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Created by Antonio J. Nebro on 23/02/14.
 */

public class ReferenceParetoFronts implements IExperimentOutput {
  public Experiment experiment_;

  public ReferenceParetoFronts(Experiment experiment) {
    experiment_ = experiment;
  }

  @Override
  public void generate() {
    for (int i = 0; i < experiment_.getProblemList().length; i++) {
      generateReferenceFronts(i);
    }
  }

  /**
   * @param problemIndex
   */
  private void generateReferenceFronts(int problemIndex) {

    File rfDirectory;
    String referenceFrontDirectory = experiment_.getExperimentBaseDirectory() + "/referenceFronts";

    rfDirectory = new File(referenceFrontDirectory);

    if (!rfDirectory.exists()) {                          // Si no existe el directorio
      boolean result = new File(referenceFrontDirectory).mkdirs();        // Lo creamos
      System.out.println("Creating " + referenceFrontDirectory);
    }

    //frontPath_[problemIndex] = referenceFrontDirectory + "/" + problemList_[problemIndex] + ".rf";
    String referenceParetoFront =
      referenceFrontDirectory + "/" + experiment_.getProblemList()[problemIndex] + ".pf";
    //String referenceParetoSet = referenceFrontDirectory + "/" + problemList_[problemIndex] + ".ps";

    MetricsUtil metricsUtils = new MetricsUtil();
    NonDominatedSolutionList solutionSet = new NonDominatedSolutionList();
    for (String anAlgorithmNameList_ : experiment_.getAlgorithmNameList()) {

      String problemDirectory =
        experiment_.getExperimentBaseDirectory() + "/data/" + anAlgorithmNameList_ +
          "/" + experiment_.getProblemList()[problemIndex];

      for (int numRun = 0; numRun < experiment_.getIndependentRuns(); numRun++) {

        String outputParetoFrontFilePath;
        outputParetoFrontFilePath = problemDirectory + "/FUN." + numRun;
        String solutionFrontFile = outputParetoFrontFilePath;

        metricsUtils.readNonDominatedSolutionSet(solutionFrontFile, solutionSet);
      } // for
    } // for
    //solutionSet.printObjectivesToFile(frontPath_[problemIndex]);
    try {
      solutionSet.printObjectivesToFile(referenceParetoFront);
    } catch (IOException e) {
      Configuration.logger_.log(Level.SEVERE, "Error", e);
    }
    //solutionSet.printVariablesToFile(referenceParetoSet);
  } // generateReferenceFronts
}
