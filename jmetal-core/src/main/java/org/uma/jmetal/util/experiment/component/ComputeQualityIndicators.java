package org.uma.jmetal.util.experiment.component;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.experiment.ExperimentComponent;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.util.experiment.util.ExperimentProblem;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.util.PointSolution;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING ;
import java.util.*;

/**
 * This class computes the {@link QualityIndicator}s of an experiment. Once the algorithms of an
 * experiment have been executed through running an instance of class {@link ExecuteAlgorithms},
 * the list of indicators in obtained from the {@link ExperimentComponent #getIndicatorsList()} method.
 * Then, for every combination algorithm + problem, the indicators are applied to all the FUN files and
 * the resulting values are store in a file called as {@link QualityIndicator #getName()}, which is located
 * in the same directory of the FUN files.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ComputeQualityIndicators<S extends Solution<?>, Result> implements ExperimentComponent {

  private final Experiment<S, Result> experiment;

  public ComputeQualityIndicators(Experiment<S, Result> experiment) {
    this.experiment = experiment ;
  }

  @Override
  public void run() throws IOException {
    for (GenericIndicator<S> indicator : experiment.getIndicatorList()) {
      JMetalLogger.logger.info("Computing indicator: " + indicator.getName()); ;

      for (ExperimentAlgorithm<?,Result> algorithm : experiment.getAlgorithmList()) {
        String algorithmDirectory ;
        algorithmDirectory = experiment.getExperimentBaseDirectory() + "/data/" +
            algorithm.getAlgorithmTag() ;

        for (int problemId = 0; problemId < experiment.getProblemList().size(); problemId++) {
          String problemDirectory = algorithmDirectory + "/" + experiment.getProblemList().get(problemId).getTag() ;

          String referenceFrontDirectory = experiment.getReferenceFrontDirectory() ;
          String referenceFrontName = referenceFrontDirectory +
              "/" + experiment.getReferenceFrontFileNames().get(problemId) ;

          JMetalLogger.logger.info("RF: " + referenceFrontName); ;
          Front referenceFront = new ArrayFront(referenceFrontName) ;

          FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront) ;
          Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront) ;

          String qualityIndicatorFile = problemDirectory + "/" + indicator.getName();
          resetFile(qualityIndicatorFile);

          indicator.setReferenceParetoFront(normalizedReferenceFront);
          for (int i = 0; i < experiment.getIndependentRuns(); i++) {
            String frontFileName = problemDirectory + "/" +
                experiment.getOutputParetoFrontFileName() + i + ".tsv";

            Front front = new ArrayFront(frontFileName) ;
            Front normalizedFront = frontNormalizer.normalize(front) ;
            List<PointSolution> normalizedPopulation = FrontUtils.convertFrontToSolutionList(normalizedFront) ;
            Double indicatorValue = (Double)indicator.evaluate((List<S>) normalizedPopulation) ;
            JMetalLogger.logger.info(indicator.getName() + ": " + indicatorValue) ;

            writeQualityIndicatorValueToFile(indicatorValue, qualityIndicatorFile) ;
          }
        }
      }
    }
    findBestIndicatorFronts(experiment) ;
  }

  private void writeQualityIndicatorValueToFile(Double indicatorValue, String qualityIndicatorFile) {
    FileWriter os;
    try {
      os = new FileWriter(qualityIndicatorFile, true);
      os.write("" + indicatorValue + "\n");
      os.close();
    } catch (IOException ex) {
      throw new JMetalException("Error writing indicator file" + ex) ;
    }
  }

  /**
   * Deletes a file or directory if it does exist
   * @param file
   */
  private void resetFile(String file) {
    File f = new File(file);
    if (f.exists()) {
      JMetalLogger.logger.info("File " + file + " exist.");

      if (f.isDirectory()) {
        JMetalLogger.logger.info("File " + file + " is a directory. Deleting directory.");
        if (f.delete()) {
          JMetalLogger.logger.info("Directory successfully deleted.");
        } else {
          JMetalLogger.logger.info("Error deleting directory.");
        }
      } else {
        JMetalLogger.logger.info("File " + file + " is a file. Deleting file.");
        if (f.delete()) {
          JMetalLogger.logger.info("File succesfully deleted.");
        } else {
          JMetalLogger.logger.info("Error deleting file.");
        }
      }
    } else {
      JMetalLogger.logger.info("File " + file + " does NOT exist.");
    }
  }

  public void findBestIndicatorFronts(Experiment<?, Result> experiment) throws IOException {
    for (GenericIndicator<?> indicator : experiment.getIndicatorList()) {
      for (ExperimentAlgorithm<?, Result> algorithm : experiment.getAlgorithmList()) {
        String algorithmDirectory;
        algorithmDirectory = experiment.getExperimentBaseDirectory() + "/data/" +
            algorithm.getAlgorithmTag();

        for (ExperimentProblem<?> problem :experiment.getProblemList()) {
          String indicatorFileName =
              algorithmDirectory + "/" + problem.getTag() + "/" + indicator.getName();
          Path indicatorFile = Paths.get(indicatorFileName) ;
          if (indicatorFile == null) {
            throw new JMetalException("Indicator file " + indicator.getName() + " doesn't exist") ;
          }

          List<String> fileArray;
          fileArray = Files.readAllLines(indicatorFile, StandardCharsets.UTF_8);

          List<Pair<Double, Integer>> list = new ArrayList<>() ;


          for (int i = 0; i < fileArray.size(); i++) {
            Pair<Double, Integer> pair = new ImmutablePair<>(Double.parseDouble(fileArray.get(i)), i) ;
            list.add(pair) ;
          }

          Collections.sort(list, new Comparator<Pair<Double, Integer>>() {
            @Override
            public int compare(Pair<Double, Integer> pair1, Pair<Double, Integer> pair2) {
              if (Math.abs(pair1.getLeft()) > Math.abs(pair2.getLeft())){
                return 1;
              } else if (Math.abs(pair1.getLeft()) < Math.abs(pair2.getLeft())) {
                return -1;
              } else {
                return 0;
              }
            }
          });
          String bestFunFileName ;
          String bestVarFileName ;
          String medianFunFileName ;
          String medianVarFileName ;

          String outputDirectory = algorithmDirectory + "/" + problem.getTag() ;

          bestFunFileName = outputDirectory + "/BEST_" + indicator.getName() + "_FUN.tsv" ;
          bestVarFileName = outputDirectory + "/BEST_" + indicator.getName() + "_VAR.tsv" ;
          medianFunFileName = outputDirectory + "/MEDIAN_" + indicator.getName() + "_FUN.tsv" ;
          medianVarFileName = outputDirectory + "/MEDIAN_" + indicator.getName() + "_VAR.tsv" ;
          if (indicator.isTheLowerTheIndicatorValueTheBetter()) {
            String bestFunFile = outputDirectory + "/" +
                experiment.getOutputParetoFrontFileName() + list.get(0).getRight() + ".tsv";
            String bestVarFile = outputDirectory + "/" +
                experiment.getOutputParetoSetFileName() + list.get(0).getRight() + ".tsv";

            Files.copy(Paths.get(bestFunFile), Paths.get(bestFunFileName), REPLACE_EXISTING) ;
            Files.copy(Paths.get(bestVarFile), Paths.get(bestVarFileName), REPLACE_EXISTING) ;
          } else {
            String bestFunFile = outputDirectory + "/" +
                experiment.getOutputParetoFrontFileName() + list.get(list.size()-1).getRight() + ".tsv";
            String bestVarFile = outputDirectory + "/" +
                experiment.getOutputParetoSetFileName() + list.get(list.size()-1).getRight() + ".tsv";

            Files.copy(Paths.get(bestFunFile), Paths.get(bestFunFileName), REPLACE_EXISTING) ;
            Files.copy(Paths.get(bestVarFile), Paths.get(bestVarFileName), REPLACE_EXISTING) ;
          }

          int medianIndex = list.size() / 2 ;
          String medianFunFile = outputDirectory + "/" +
              experiment.getOutputParetoFrontFileName() + list.get(medianIndex).getRight() + ".tsv";
          String medianVarFile = outputDirectory + "/" +
              experiment.getOutputParetoSetFileName() + list.get(medianIndex).getRight() + ".tsv";

          Files.copy(Paths.get(medianFunFile), Paths.get(medianFunFileName), REPLACE_EXISTING) ;
          Files.copy(Paths.get(medianVarFile), Paths.get(medianVarFileName), REPLACE_EXISTING) ;
        }
      }
    }
  }
}

