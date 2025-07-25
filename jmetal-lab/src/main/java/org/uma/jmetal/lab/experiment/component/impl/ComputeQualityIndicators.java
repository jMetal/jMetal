package org.uma.jmetal.lab.experiment.component.impl;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.component.ExperimentComponent;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * This class computes the {@link QualityIndicator}s of an org.uma.jmetal.experiment. Once the
 * algorithms of an org.uma.jmetal.experiment have been executed through running an instance of
 * class {@link ExecuteAlgorithms}, the list of indicators in obtained from the {@link
 * ExperimentComponent #getIndicatorsList()} method. Then, for every combination algorithm +
 * problem, the indicators are applied to all the FUN files and the resulting values are store in a
 * file called as {@link QualityIndicator #getName()}, which is located in the same directory of the
 * FUN files.
 *
 * @author Antonio J. Nebro
 */
public class ComputeQualityIndicators<S extends Solution<?>, Result extends List<S>>
    implements ExperimentComponent {
  private final Experiment<S, Result> experiment;

  public ComputeQualityIndicators(Experiment<S, Result> experiment) {
    this.experiment = experiment;
  }

  @Override
  public void run() throws IOException {
    experiment.removeDuplicatedAlgorithms();
    resetIndicatorFiles();

    for (QualityIndicator indicator : experiment.getIndicatorList()) {
      JMetalLogger.logger.info("Computing indicator: " + indicator.name());

      for (ExperimentAlgorithm<?, Result> algorithm : experiment.getAlgorithmList()) {
        String algorithmDirectory;
        algorithmDirectory =
            experiment.getExperimentBaseDirectory() + "/data/" + algorithm.getAlgorithmTag();
        for (ExperimentProblem<?> problem : experiment.getProblemList()) {
          String problemDirectory = algorithmDirectory + "/" + problem.getTag();

          String referenceFrontDirectory = experiment.getReferenceFrontDirectory();

          String referenceFrontName = referenceFrontDirectory + "/" + problem.getReferenceFront();

          JMetalLogger.logger.info("RF: " + referenceFrontName);

          double[][] referenceFront = VectorUtils.readVectors(referenceFrontName, ",");
          double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);

          indicator.referenceFront(normalizedReferenceFront);

          String qualityIndicatorFile = problemDirectory + "/" + indicator.name();

          // indicator.setReferenceParetoFront(normalizedReferenceFront);

          double[] indicatorValues = new double[experiment.getIndependentRuns()];
          IntStream.range(0, experiment.getIndependentRuns()).parallel()
              .forEach(
                  run -> {
                    String frontFileName =
                        problemDirectory
                            + "/"
                            + experiment.getOutputParetoFrontFileName()
                            + run
                            + ".csv";
                    double[][] front = new double[0][];
                    try {
                      front = VectorUtils.readVectors(frontFileName, ",");
                    } catch (IOException e) {
                      e.printStackTrace();
                    }

                    double[][] normalizedFront =
                        NormalizeUtils.normalize(
                            front,
                            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
                            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));
                    double indicatorValue = indicator.compute(normalizedFront);
                    JMetalLogger.logger.info(indicator.name() + ": " + indicatorValue);
                    indicatorValues[run] = indicatorValue;
                  });

          for (double indicatorValue : indicatorValues) {
            writeQualityIndicatorValueToFile(indicatorValue, qualityIndicatorFile);
          }
        }
      }
    }
    findBestIndicatorFronts(experiment);
    writeSummaryFile(experiment);
  }

  private void writeQualityIndicatorValueToFile(
      Double indicatorValue, String qualityIndicatorFile) {

    try (FileWriter os = new FileWriter(qualityIndicatorFile, true)) {
      os.write("" + indicatorValue + "\n");
    } catch (IOException ex) {
      throw new JMetalException("Error writing indicator file" + ex);
    }
  }

  public void findBestIndicatorFronts(Experiment<?, Result> experiment) throws IOException {
    for (QualityIndicator indicator : experiment.getIndicatorList()) {
      for (ExperimentAlgorithm<?, Result> algorithm : experiment.getAlgorithmList()) {
        String algorithmDirectory;
        algorithmDirectory =
            experiment.getExperimentBaseDirectory() + "/data/" + algorithm.getAlgorithmTag();

        for (ExperimentProblem<?> problem : experiment.getProblemList()) {
          String indicatorFileName =
              algorithmDirectory + "/" + problem.getTag() + "/" + indicator.name();
          Path indicatorFile = Paths.get(indicatorFileName);

          List<String> fileArray;
          fileArray = Files.readAllLines(indicatorFile, StandardCharsets.UTF_8);

          List<Pair<Double, Integer>> list = new ArrayList<>();

          for (int i = 0; i < fileArray.size(); i++) {
            Pair<Double, Integer> pair =
                new ImmutablePair<>(Double.parseDouble(fileArray.get(i)), i);
            list.add(pair);
          }

          list.sort(Comparator.comparingDouble(pair -> Math.abs(pair.getLeft())));
          String bestFunFileName;
          String bestVarFileName;
          String medianFunFileName;
          String medianVarFileName;

          String outputDirectory = algorithmDirectory + "/" + problem.getTag();

          bestFunFileName = outputDirectory + "/BEST_" + indicator.name() + "_FUN.csv";
          bestVarFileName = outputDirectory + "/BEST_" + indicator.name() + "_VAR.csv";
          medianFunFileName = outputDirectory + "/MEDIAN_" + indicator.name() + "_FUN.csv";
          medianVarFileName = outputDirectory + "/MEDIAN_" + indicator.name() + "_VAR.csv";
          if (indicator.isTheLowerTheIndicatorValueTheBetter()) {
            String bestFunFile =
                outputDirectory
                    + "/"
                    + experiment.getOutputParetoFrontFileName()
                    + list.get(0).getRight()
                    + ".csv";
            String bestVarFile =
                outputDirectory
                    + "/"
                    + experiment.getOutputParetoSetFileName()
                    + list.get(0).getRight()
                    + ".csv";

            Files.copy(Paths.get(bestFunFile), Paths.get(bestFunFileName), REPLACE_EXISTING);
            Files.copy(Paths.get(bestVarFile), Paths.get(bestVarFileName), REPLACE_EXISTING);
          } else {
            String bestFunFile =
                outputDirectory
                    + "/"
                    + experiment.getOutputParetoFrontFileName()
                    + list.get(list.size() - 1).getRight()
                    + ".csv";
            String bestVarFile =
                outputDirectory
                    + "/"
                    + experiment.getOutputParetoSetFileName()
                    + list.get(list.size() - 1).getRight()
                    + ".csv";

            Files.copy(Paths.get(bestFunFile), Paths.get(bestFunFileName), REPLACE_EXISTING);
            Files.copy(Paths.get(bestVarFile), Paths.get(bestVarFileName), REPLACE_EXISTING);
          }

          int medianIndex = list.size() / 2;
          String medianFunFile =
              outputDirectory
                  + "/"
                  + experiment.getOutputParetoFrontFileName()
                  + list.get(medianIndex).getRight()
                  + ".csv";
          String medianVarFile =
              outputDirectory
                  + "/"
                  + experiment.getOutputParetoSetFileName()
                  + list.get(medianIndex).getRight()
                  + ".csv";

          Files.copy(Paths.get(medianFunFile), Paths.get(medianFunFileName), REPLACE_EXISTING);
          Files.copy(Paths.get(medianVarFile), Paths.get(medianVarFileName), REPLACE_EXISTING);
        }
      }
    }
  }

  /** Deletes the files containing the indicator values if the exist. */
  private void resetIndicatorFiles() {
    for (QualityIndicator indicator : experiment.getIndicatorList()) {
      for (ExperimentAlgorithm<?, Result> algorithm : experiment.getAlgorithmList()) {
        for (ExperimentProblem<?> problem : experiment.getProblemList()) {
          String algorithmDirectory;
          algorithmDirectory =
              experiment.getExperimentBaseDirectory() + "/data/" + algorithm.getAlgorithmTag();
          String problemDirectory = algorithmDirectory + "/" + problem.getTag();
          String qualityIndicatorFile = problemDirectory + "/" + indicator.name();

          resetFile(qualityIndicatorFile);
        }
      }
    }
  }

  /**
   * Deletes a file or directory if it does exist
   *
   * @param file
   */
  private void resetFile(String file) {
    File f = new File(file);
    if (f.exists()) {
      JMetalLogger.logger.info("Already existing file " + file);

      if (f.isDirectory()) {
        JMetalLogger.logger.info("Deleting directory " + file);
        if (f.delete()) {
          JMetalLogger.logger.info("Directory successfully deleted.");
        } else {
          JMetalLogger.logger.info("Error deleting directory.");
        }
      } else {
        JMetalLogger.logger.info("Deleting file " + file);
        if (f.delete()) {
          JMetalLogger.logger.info("File successfully deleted.");
        } else {
          JMetalLogger.logger.info("Error deleting file.");
        }
      }
    } else {
      JMetalLogger.logger.info("File " + file + " does NOT exist.");
    }
  }

  private void writeSummaryFile(Experiment<S, Result> experiment) {
    JMetalLogger.logger.info("Writing org.uma.jmetal.experiment summary file");
    String headerOfCSVFile = "Algorithm,Problem,IndicatorName,ExecutionId,IndicatorValue";
    String csvFileName =
        this.experiment.getExperimentBaseDirectory() + "/QualityIndicatorSummary.csv";
    resetFile(csvFileName);

    try (FileWriter os = new FileWriter(csvFileName, true)) {
      os.write("" + headerOfCSVFile + "\n");

      for (QualityIndicator indicator : experiment.getIndicatorList()) {
        for (ExperimentAlgorithm<?, Result> algorithm : experiment.getAlgorithmList()) {
          String algorithmDirectory;
          algorithmDirectory =
              experiment.getExperimentBaseDirectory() + "/data/" + algorithm.getAlgorithmTag();

          for (ExperimentProblem<?> problem : experiment.getProblemList()) {
            String indicatorFileName =
                algorithmDirectory + "/" + problem.getTag() + "/" + indicator.name();
            Path indicatorFile = Paths.get(indicatorFileName);
            if (indicatorFile == null) {
              throw new JMetalException("Indicator file " + indicator.name() + " doesn't exist");
            }
            System.out.println("-----");
            System.out.println(indicatorFileName);

            List<String> fileArray;
            fileArray = Files.readAllLines(indicatorFile, StandardCharsets.UTF_8);
            System.out.println(fileArray);
            System.out.println("++++++");

            for (int i = 0; i < fileArray.size(); i++) {
              String row =
                  algorithm.getAlgorithmTag()
                      + ","
                      + problem.getTag()
                      + ","
                      + indicator.name()
                      + ","
                      + i
                      + ","
                      + fileArray.get(i);
              os.write("" + row + "\n");
            }
          }
        }
      }
    } catch (IOException ex) {
      throw new JMetalException("Error writing indicator file" + ex);
    }
  }
}
