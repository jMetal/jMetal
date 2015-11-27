package org.uma.jmetal.util.experiment.component;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.ExperimentConfiguration;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class computes a reference Pareto front from a set of files.
 *
 * @author Antonio J. Nebro
 */
public class GenerateReferenceParetoFront implements ExperimentComponent{
  private static final String DEFAULT_OUTPUT_DIRECTORY = "referenceFronts" ;

  private final ExperimentConfiguration<?, ?> experimentConfiguration;
  
  public GenerateReferenceParetoFront(ExperimentConfiguration experimentConfiguration) {
    this.experimentConfiguration = experimentConfiguration ;
  }

  /**
   * This method creates de output directory and compute the fronts
   */
  @Override
  public void run() throws IOException {
    String outputDirectoryName ;
    outputDirectoryName = experimentConfiguration.getExperimentBaseDirectory() + "/" + DEFAULT_OUTPUT_DIRECTORY ;

    File outputDirectory = createOutputDirectory(outputDirectoryName) ;

    for (Problem<?> problem : experimentConfiguration.getProblemList()) {
      NonDominatedSolutionListArchive<DoubleSolution> nonDominatedSolutionArchive =
          new NonDominatedSolutionListArchive<DoubleSolution>() ;

      for (TaggedAlgorithm<?> algorithm : experimentConfiguration.getAlgorithmList()) {
        String problemDirectory = experimentConfiguration.getExperimentBaseDirectory() + "/data/" +
            algorithm.getTag() + "/" + problem.getName() ;

        for (int i = 0 ; i < experimentConfiguration.getIndependentRuns(); i++) {
          String frontFileName = problemDirectory + "/" + experimentConfiguration.getOutputParetoFrontFileName() +
              i + ".tsv";
          System.out.println(frontFileName) ;
          Front front = new ArrayFront(frontFileName) ;
          List<DoubleSolution> solutionList = FrontUtils.convertFrontToSolutionList(front) ;
          for (DoubleSolution solution : solutionList) {
            nonDominatedSolutionArchive.add(solution) ;
          }
        }
      }
      String referenceSetFileName = outputDirectoryName + "/" + problem.getName() + ".rf" ;
      new SolutionListOutput(nonDominatedSolutionArchive.getSolutionList())
          .printObjectivesToFile(referenceSetFileName); ;
    }
  }

  private File createOutputDirectory(String outputDirectoryName) {
    File outputDirectory ;
    outputDirectory = new File(outputDirectoryName) ;
    if (!outputDirectory.exists()) {
      boolean result = new File(outputDirectoryName).mkdir() ;
      JMetalLogger.logger.info("Creating " + outputDirectoryName + ". Status = " + result);
    }

    return outputDirectory ;
  }
}
