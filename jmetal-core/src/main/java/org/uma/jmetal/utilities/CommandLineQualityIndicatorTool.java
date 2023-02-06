package org.uma.jmetal.utilities;

import static org.uma.jmetal.qualityindicator.QualityIndicatorUtils.getAvailableIndicators;
import static org.uma.jmetal.qualityindicator.QualityIndicatorUtils.getIndicatorFromName;

import java.io.IOException;
import java.util.List;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.SetCoverage;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class for executing quality indicators from the command line. The program requires two arguments:
 * - the name of the quality indicator (HV, EP, etc., o ALL to compute all of them
 * - the file containing a reference front
 * - the file whose Pareto front approximation is going to be analyzed
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class CommandLineQualityIndicatorTool {
  public static void main(String[] args) throws Exception {
    checkArguments(args);
    calculateAndPrintIndicators(args);
  }

  /**
   * Check the argument length
   *
   * @param args
   */
  private static void checkArguments(String[] args) {
    if (args.length != 3) {
      printAvailableIndicators();
      throw new JMetalException("Invalid arguments");
    }
  }

  /** Prints the command line options in the screen */
  private static void printAvailableIndicators() {
    JMetalLogger.logger.info(
        "Parameters: indicatorName referenceFrontFile frontFile \n"
            + "Where indicatorValue can be one of these:\n"
            + "EP   - Additive epsilon\n"
            + "GD   - Generational distance\n"
            + "IGD  - Inverted generational distance\n"
            + "IGD+ - Inverted generational distance plus \n"
            + "HV   - Hypervolume \n"
            + "ER   - Error ratio \n"
            + "SP   - Spread (two objectives)\n"
            + "GSPREAD - Generalized Spread (more than two objectives)\n"
            + "SC   - Set coverage\n"
            );
  }

  /**
   * Compute the quality indicator(s) and prints it (them)
   *
   * @param args
   */
  private static void calculateAndPrintIndicators(String[] args) throws IOException {
    double[][] referenceFront = VectorUtils.readVectors(args[1], ",");
    double[][] front = VectorUtils.readVectors(args[2],",");

    double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
    double[][] normalizedFront =
        NormalizeUtils.normalize(
            front,
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    JMetalLogger.logger.info("The fronts are normalized before computing the indicators");

    List<QualityIndicator> indicatorList = getAvailableIndicators(normalizedReferenceFront);

    if (!args[0].equals("ALL")) {
      QualityIndicator indicator = getIndicatorFromName(args[0], indicatorList);
      JMetalLogger.logger.info(() -> "" + indicator.compute(normalizedFront));
    } else {
      for (QualityIndicator indicator : indicatorList) {
        JMetalLogger.logger.info(() -> indicator.name() + ": " + indicator.compute(normalizedFront));
      }

      SetCoverage sc = new SetCoverage();
      JMetalLogger.logger.info(
          () -> "SC(refFront, front): " + sc.compute(normalizedReferenceFront, normalizedFront));
      JMetalLogger.logger.info(
          () -> "SC(front, refFront): " + sc.compute(normalizedFront, normalizedReferenceFront));
    }
  }
}
