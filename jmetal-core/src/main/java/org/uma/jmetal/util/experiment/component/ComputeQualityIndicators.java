package org.uma.jmetal.util.experiment.component;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.ExperimentConfiguration;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class computes a reference Pareto front from a set of files.
 *
 * @author Antonio J. Nebro
 */
public class ComputeQualityIndicators implements ExperimentComponent{

  private final ExperimentConfiguration<?, ?> configuration;
  private List<GenericIndicator<List<? extends Solution<?>>>> indicatorList ;

  public ComputeQualityIndicators(ExperimentConfiguration experimentConfiguration,
                                  List<GenericIndicator<List<? extends Solution<?>>>> indicatorList) {
    this.configuration = experimentConfiguration ;
    this.indicatorList = indicatorList ;
  }

  @Override
  public void run() throws IOException {
    for (GenericIndicator indicator : indicatorList) {
      System.out.println(indicator.getName()) ;

      for (TaggedAlgorithm algorithm : configuration.getAlgorithmList()) {
        String algorithmDirectory ;
        algorithmDirectory = configuration.getExperimentBaseDirectory() + "/data/" +
            algorithm.getTag() ;

        for (int problemId = 0 ; problemId < configuration.getProblemList().size(); problemId++) {
          String problemDirectory = algorithmDirectory + "/" + configuration.getProblemList().get(problemId).getName() ;

          //resetFile(problemDirectory);
          String referenceFrontDirectory = configuration.getReferenceFrontDirectory() ;
          String referenceFrontName = referenceFrontDirectory +
              "/" + configuration.getReferenceFrontFileNames().get(problemId) ;

          Front referenceFront = new ArrayFront(referenceFrontName) ;
          Front normalizedReferenceFront = new FrontNormalizer(referenceFront).normalize(referenceFront) ;

          indicator.setReferenceParetoFront(normalizedReferenceFront);
          for (int i = 0; i < configuration.getIndependentRuns(); i++) {
            String frontFileName;
            frontFileName = problemDirectory + "/" +
                configuration.getOutputParetoFrontFileName() + i + ".tsv";
            String qualityIndicatorFile = problemDirectory + "/" + indicator.getName();
            System.out.println("Indicator file: " + qualityIndicatorFile) ;
            Front front = new ArrayFront(frontFileName) ;
            Front normalizedFront = new FrontNormalizer(normalizedReferenceFront).normalize(front) ;
            Double indicatorValue = (Double)indicator.evaluate(FrontUtils.convertFrontToSolutionList(normalizedFront)) ;
            System.out.println(indicator.getName() + ": " + indicatorValue) ;

            FileWriter os;
            try {
              os = new FileWriter(qualityIndicatorFile, true);
              os.write("" + indicatorValue + "\n");
              os.close();
            } catch (IOException ex) {
              throw new JMetalException("Error writing indicator file" + ex) ;
            }
          }

        }
      }
    }
  }

  /**
   * Deletes a file or directory if it does exist
   * @param file
   */
  private void resetFile(String file) {
    File f = new File(file);
    if (f.exists()) {
      System.out.println("File " + file + " exist.");

      if (f.isDirectory()) {
        System.out.println("File " + file + " is a directory. Deleting directory.");
        if (f.delete()) {
          System.out.println("Directory successfully deleted.");
        } else {
          System.out.println("Error deleting directory.");
        }
      } else {
        System.out.println("File " + file + " is a file. Deleting file.");
        if (f.delete()) {
          System.out.println("File succesfully deleted.");
        } else {
          System.out.println("Error deleting file.");
        }
      }
    } else {
      System.out.println("File " + file + " does NOT exist.");
    }
  } // resetFile
}
