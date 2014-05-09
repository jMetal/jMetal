package jmetal.experiments.util ;

import jmetal.experiments.Experiment;
import jmetal.qualityIndicator.util.MetricsUtil;
import jmetal.util.NonDominatedSolutionList;

import java.io.File;
import java.io.IOException;

/**
 * Created by Antonio J. Nebro on 23/02/14.
 */

public class ReferenceParetoFronts implements iExperimentOutput {
  public Experiment experiment_ ;

  public ReferenceParetoFronts(Experiment experiment) {
    experiment_ = experiment ;
  }

  @Override
  public void generate() {
    for (int i = 0; i < experiment_.problemList_.length; i++) {
       generateReferenceFronts(i);
    }
  }

  /**
   * @param problemIndex
   */
  private void generateReferenceFronts(int problemIndex) {

    File rfDirectory;
    String referenceFrontDirectory = experiment_.experimentBaseDirectory_ + "/referenceFronts";

    rfDirectory = new File(referenceFrontDirectory);

    if (!rfDirectory.exists()) {                          // Si no existe el directorio
      boolean result = new File(referenceFrontDirectory).mkdirs();        // Lo creamos
      System.out.println("Creating " + referenceFrontDirectory);
    }

    //frontPath_[problemIndex] = referenceFrontDirectory + "/" + problemList_[problemIndex] + ".rf";
    String referenceParetoFront = referenceFrontDirectory + "/" + experiment_.problemList_[problemIndex] + ".pf";
    //String referenceParetoSet = referenceFrontDirectory + "/" + problemList_[problemIndex] + ".ps";

    MetricsUtil metricsUtils = new MetricsUtil();
    NonDominatedSolutionList solutionSet = new NonDominatedSolutionList();
    for (String anAlgorithmNameList_ : experiment_.algorithmNameList_) {

      String problemDirectory = experiment_.experimentBaseDirectory_ + "/data/" + anAlgorithmNameList_ +
              "/" + experiment_.problemList_[problemIndex];

      for (int numRun = 0; numRun < experiment_.independentRuns_; numRun++) {

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
      e.printStackTrace();
    }
    //solutionSet.printVariablesToFile(referenceParetoSet);
  } // generateReferenceFronts
}
