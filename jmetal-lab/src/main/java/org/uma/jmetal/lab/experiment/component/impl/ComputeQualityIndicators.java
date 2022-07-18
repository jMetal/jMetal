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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
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
 * This class computes the {@link QualityIndicator}s of an org.uma.jmetal.experiment. Once the algorithms of an
 * org.uma.jmetal.experiment have been executed through running an instance of class {@link ExecuteAlgorithms},
 * the list of indicators in obtained from the {@link ExperimentComponent #getIndicatorsList()} method.
 * Then, for every combination algorithm + problem, the indicators are applied to all the FUN files and
 * the resulting values are store in a file called as {@link QualityIndicator #getName()}, which is located
 * in the same directory of the FUN files.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ComputeQualityIndicators<S extends Solution<?>, Result extends List<S>> implements ExperimentComponent {
  private final Experiment<S, Result> experiment;

  public ComputeQualityIndicators(Experiment<S, Result> experiment) {
    this.experiment = experiment;
  }

  @Override
  public void run() throws IOException {
    experiment.removeDuplicatedAlgorithms();
    resetIndicatorFiles();

    for (var indicator : experiment.getIndicatorList()) {
      JMetalLogger.logger.info("Computing indicator: " + indicator.getName());

      for (@NotNull ExperimentAlgorithm<?, Result> algorithm : experiment.getAlgorithmList()) {
        var algorithmDirectory = experiment.getExperimentBaseDirectory() + "/data/" + algorithm.getAlgorithmTag();
        for (@NotNull ExperimentProblem<?> problem : experiment.getProblemList()) {
          var problemDirectory = algorithmDirectory + "/" + problem.getTag();

          var referenceFrontDirectory = experiment.getReferenceFrontDirectory();

          var referenceFrontName = referenceFrontDirectory + "/" + problem.getReferenceFront();

          JMetalLogger.logger.info("RF: " + referenceFrontName);

          var referenceFront = VectorUtils.readVectors(referenceFrontName, ",");
          var normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);

          indicator.setReferenceFront(normalizedReferenceFront);


          //Front referenceFront = new ArrayFront(referenceFrontName, ",");

          //FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront);
          //Front normalizedReferenceFront = frontNormalizer.normalize(referenceFront);

          var qualityIndicatorFile = problemDirectory + "/" + indicator.getName();

          //indicator.setReferenceParetoFront(normalizedReferenceFront);

          var indicatorValues = new double[experiment.getIndependentRuns()];
          var bound = experiment.getIndependentRuns();
          for (var run = 0; run < bound; run++) {
            var frontFileName = problemDirectory + "/" +
                    experiment.getOutputParetoFrontFileName() + run + ".csv";
            var front = new double[0][];
            try {
              front = VectorUtils.readVectors(frontFileName, ",");
            } catch (IOException e) {
              e.printStackTrace();
            }

            var normalizedFront =
                    NormalizeUtils.normalize(
                            front,
                            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
                            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));
            Double indicatorValue = indicator.compute(normalizedFront);
            JMetalLogger.logger.info(indicator.getName() + ": " + indicatorValue);
            indicatorValues[run] = indicatorValue;
          }

          for (var indicatorValue : indicatorValues) {
            writeQualityIndicatorValueToFile(indicatorValue, qualityIndicatorFile);
          }

          /*
          for (int run = 0; run < experiment.getIndependentRuns(); run++) {
            String frontFileName = problemDirectory + "/" +
                experiment.getOutputParetoFrontFileName() + run + ".csv";

            Front front = new ArrayFront(frontFileName, ",");
            Front normalizedFront = frontNormalizer.normalize(front);
            List<PointSolution> normalizedPopulation = FrontUtils.convertFrontToSolutionList(normalizedFront);
            Double indicatorValue = (Double) indicator.evaluate((List<S>) normalizedPopulation);
            JMetalLogger.logger.info(indicator.getName() + ": " + indicatorValue);

            writeQualityIndicatorValueToFile(indicatorValue, qualityIndicatorFile);
          }
           */
        }
      }
    }
    findBestIndicatorFronts(experiment);
    writeSummaryFile(experiment);
  }

  private void writeQualityIndicatorValueToFile(Double indicatorValue, String qualityIndicatorFile) {

    try (@NotNull FileWriter os = new FileWriter(qualityIndicatorFile, true)) {
      os.write("" + indicatorValue + "\n");
    } catch (IOException ex) {
      throw new JMetalException("Error writing indicator file" + ex);
    }
  }

  public void findBestIndicatorFronts(@NotNull Experiment<?, Result> experiment) throws IOException {
    for (var indicator : experiment.getIndicatorList()) {
      for (ExperimentAlgorithm<?, Result> algorithm : experiment.getAlgorithmList()) {
        var algorithmDirectory = experiment.getExperimentBaseDirectory() + "/data/" +
                algorithm.getAlgorithmTag();

        for (ExperimentProblem<?> problem : experiment.getProblemList()) {
          var indicatorFileName =
                  algorithmDirectory + "/" + problem.getTag() + "/" + indicator.getName();
          var indicatorFile = Paths.get(indicatorFileName);

          var fileArray = Files.readAllLines(indicatorFile, StandardCharsets.UTF_8);

          List<Pair<Double, Integer>> list = new ArrayList<>();
          var bound = fileArray.size();
          for (var i = 0; i < bound; i++) {
            var doubleIntegerImmutablePair = new ImmutablePair<Double, Integer>(Double.parseDouble(fileArray.get(i)), i);
            list.add(doubleIntegerImmutablePair);
          }
          list.sort(Comparator.comparingDouble(pair -> Math.abs(pair.getLeft())));

          var outputDirectory = algorithmDirectory + "/" + problem.getTag();

          var bestFunFileName = outputDirectory + "/BEST_" + indicator.getName() + "_FUN.csv";
          var bestVarFileName = outputDirectory + "/BEST_" + indicator.getName() + "_VAR.csv";
          var medianFunFileName = outputDirectory + "/MEDIAN_" + indicator.getName() + "_FUN.csv";
          var medianVarFileName = outputDirectory + "/MEDIAN_" + indicator.getName() + "_VAR.csv";
          if (indicator.isTheLowerTheIndicatorValueTheBetter()) {
            var bestFunFile = outputDirectory + "/" +
                    experiment.getOutputParetoFrontFileName() + list.get(0).getRight() + ".csv";
            var bestVarFile = outputDirectory + "/" +
                    experiment.getOutputParetoSetFileName() + list.get(0).getRight() + ".csv";

            Files.copy(Paths.get(bestFunFile), Paths.get(bestFunFileName), REPLACE_EXISTING);
            Files.copy(Paths.get(bestVarFile), Paths.get(bestVarFileName), REPLACE_EXISTING);
          } else {
            @NotNull String bestFunFile = outputDirectory + "/" +
                    experiment.getOutputParetoFrontFileName() + list.get(list.size() - 1).getRight() + ".csv";
            var bestVarFile = outputDirectory + "/" +
                    experiment.getOutputParetoSetFileName() + list.get(list.size() - 1).getRight() + ".csv";

            Files.copy(Paths.get(bestFunFile), Paths.get(bestFunFileName), REPLACE_EXISTING);
            Files.copy(Paths.get(bestVarFile), Paths.get(bestVarFileName), REPLACE_EXISTING);
          }

          var medianIndex = list.size() / 2;
          var medianFunFile = outputDirectory + "/" +
                  experiment.getOutputParetoFrontFileName() + list.get(medianIndex).getRight() + ".csv";
          var medianVarFile = outputDirectory + "/" +
                  experiment.getOutputParetoSetFileName() + list.get(medianIndex).getRight() + ".csv";

          Files.copy(Paths.get(medianFunFile), Paths.get(medianFunFileName), REPLACE_EXISTING);
          Files.copy(Paths.get(medianVarFile), Paths.get(medianVarFileName), REPLACE_EXISTING);
        }
      }
    }
  }

  /**
   * Deletes the files containing the indicator values if the exist.
   */
  private void resetIndicatorFiles() {
    for (@NotNull QualityIndicator indicator : experiment.getIndicatorList()) {
      for (ExperimentAlgorithm<?, Result> algorithm : experiment.getAlgorithmList()) {
        for (ExperimentProblem<?> problem : experiment.getProblemList()) {
          var algorithmDirectory = experiment.getExperimentBaseDirectory() + "/data/" + algorithm.getAlgorithmTag();
          var problemDirectory = algorithmDirectory + "/" + problem.getTag();
          var qualityIndicatorFile = problemDirectory + "/" + indicator.getName();

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
    var f = new File(file);
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
    var headerOfCSVFile = "Algorithm,Problem,IndicatorName,ExecutionId,IndicatorValue";
    var csvFileName = this.experiment.getExperimentBaseDirectory() + "/QualityIndicatorSummary.csv";
    resetFile(csvFileName);

    try (var os = new FileWriter(csvFileName, true)) {
      os.write("" + headerOfCSVFile + "\n");

      for (@NotNull QualityIndicator indicator : experiment.getIndicatorList()) {
        for (@NotNull ExperimentAlgorithm<?, Result> algorithm : experiment.getAlgorithmList()) {
          var algorithmDirectory = experiment.getExperimentBaseDirectory() + "/data/" +
                  algorithm.getAlgorithmTag();

          for (ExperimentProblem<?> problem : experiment.getProblemList()) {
            @NotNull String indicatorFileName =
                    algorithmDirectory + "/" + problem.getTag() + "/" + indicator.getName();
            @NotNull Path indicatorFile = Paths.get(indicatorFileName);
            if (indicatorFile == null) {
              throw new JMetalException("Indicator file " + indicator.getName() + " doesn't exist");
            }
            System.out.println("-----");
            System.out.println(indicatorFileName);

            var fileArray = Files.readAllLines(indicatorFile, StandardCharsets.UTF_8);
            System.out.println(fileArray);
            System.out.println("++++++");

            for (var i = 0; i < fileArray.size(); i++) {
              @NotNull String row = algorithm.getAlgorithmTag() + "," + problem.getTag() + "," + indicator.getName() + "," + i + "," + fileArray.get(i);
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

