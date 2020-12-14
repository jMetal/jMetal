package org.uma.jmetal.utilities;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for executing quality indicators from the command line. An optional argument allows to
 * indicate whether the fronts are to be normalized by the quality indicators.
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
      printOptions();
      throw new JMetalException("Incorrect arguments");
    }
  }

  /** Prints the command line options in the screen */
  private static void printOptions() {
    JMetalLogger.logger.info(
        "Parameters: indicatorName referenceFront front \" \n\n"
            + "Where indicatorValue can be one of these:\n"
            + "GD   - Generational distance\n"
            + "IGD  - Inverted generational distance\n"
            + "IGD  - Inverted generational distance\n"
            + "IGD+ - Inverted generational distance plus \n"
            + "HV   - Hypervolume \n"
            + "ER   - Error ratio \n"
            + "SPREAD  - Spread (two objectives)\n"
            + "GSPREAD - Generalized Spread (more than two objectives)\n"
            //+ "ER   - Error ratio\n"
            // + "R2   - R2\n\n" + "ALL  - prints all the available indicators \n\n"
            );
  }

  /**
   * Compute the quality indicator(s) and prints it (them)
   *
   * @param args
   */
  private static void calculateAndPrintIndicators(String[] args) {
    double[][] referenceFront = VectorUtils.readVectors(args[1], ",");
    double[][] front = VectorUtils.readVectors(args[2],",");

    double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
    double[][] normalizedFront =
        NormalizeUtils.normalize(
            front,
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    JMetalLogger.logger.info("The fronts are NORMALIZED before computing the indicators");

    List<QualityIndicator> indicatorList = getAvailableIndicators(normalizedReferenceFront);

    if (!args[0].equals("ALL")) {
      QualityIndicator indicator = getIndicatorFromName(args[0], indicatorList);
      System.out.println(indicator.compute(normalizedFront));
    } else {
      for (QualityIndicator indicator : indicatorList) {
        System.out.println(indicator.getName() + ": " + indicator.compute(normalizedFront));
      }

      SetCoverage sc = new SetCoverage();
      JMetalLogger.logger.info(
          "SC(refPF, front): " + sc.compute(normalizedReferenceFront, normalizedFront));
      JMetalLogger.logger.info(
          "SC(front, refPF): " + sc.compute(normalizedFront, normalizedReferenceFront));
    }
  }

  /**
   * Creates a list with the available indicators (but setCoverage)
   *
   * @param referenceFront
   * @return
   */
  private static List<QualityIndicator> getAvailableIndicators(double[][] referenceFront) {

    List<QualityIndicator> list = new ArrayList<>();
    list.add(new Epsilon(referenceFront));
    list.add(new PISAHypervolume(referenceFront));
    list.add(new NormalizedHypervolume(referenceFront));
    list.add(new GenerationalDistance(referenceFront));
    list.add(new InvertedGenerationalDistance(referenceFront));
    list.add(new InvertedGenerationalDistancePlus(referenceFront));
    list.add(new Spread(referenceFront));
    list.add(new GeneralizedSpread(referenceFront));
    list.add(new R2(referenceFront));

    return list;
  }

  /**
   * Given an indicator name, finds the indicator in the list of indicator
   *
   * @param name
   * @param list
   * @return
   */
  private static QualityIndicator getIndicatorFromName(String name, List<QualityIndicator> list) {
    QualityIndicator result = null;

    for (QualityIndicator indicator : list) {
      if (indicator.getName().equals(name)) {
        result = indicator;
        break;
      }
    }

    Check.isNotNull(result);

    return result;
  }
}
