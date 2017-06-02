package org.uma.jmetal.qualityIndicator;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontNormalizer;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.util.PointSolution;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for executing quality indicators from the command line. An optional argument allows to
 * indicate whether the fronts are to be normalized by the quality indicators.
 *
 * Invoking command:
   mvn
   -pl jmetal-exec
   exec:java -Dexec.mainClass="org.uma.jmetal.qualityIndicator.CommandLineIndicatorRunner"
   -Dexec.args="indicator referenceFront front normalize"
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class CommandLineIndicatorRunner {
  public static void main(String[] args) throws Exception {
    boolean normalize ;

    checkArguments(args) ;
    normalize = checkAboutNormalization(args) ;
    calculateAndPrintIndicators(args, normalize) ;
  }

  /**
   * Check the argument length
   * @param args
   */
  private static void checkArguments(String[] args) {
    if ((args.length != 3) && (args.length != 4)) {
      printOptions();
      throw new JMetalException("Incorrect arguments");
    }
  }

  /**
   * Checks if normalization is set
   * @param args
   * @return
   */
  private static boolean checkAboutNormalization(String args[]) {
    boolean normalize = false ;
    if (args.length == 4) {
      if (args[3].equals("TRUE")) {
        normalize = true;
      } else if (args[3].equals("FALSE")) {
        normalize = false;
      } else {
        throw new JMetalException("The value for normalizing must be TRUE or FALSE");
      }
    }

    return normalize ;
  }

  /**
   * Prints the command line options in the screen
   */
  private static void printOptions() {
    JMetalLogger.logger.info("Usage: mvn -pl jmetal-exec exec:java -Dexec:mainClass=\"org.uma.jmetal.qualityIndicator.RunIndicator\""
        + "-Dexec.args=\"indicatorName referenceFront front [normalize]\" \n\n"
        + "Where indicatorValue can be one of these:\n" + "GD   - Generational distance\n"
        + "IGD  - Inverted generational distance\n" + "IGD  - Inverted generational distance\n"
        + "IGD+ - Inverted generational distance plus \n" + "HV   - Hypervolume \n"
        + "ER   - Error ratio \n" + "SPREAD  - Spread (two objectives)\n"
        + "GSPREAD - Generalized Spread (more than two objectives)\n" + "ER   - Error ratio\n"
        //+ "R2   - R2\n\n" + "ALL  - prints all the available indicators \n\n"
        + "Normalize can be TRUE or FALSE (the fronts are normalized before computing"
        + " the indicators) \n") ;
  }

  /**
   * Compute the quality indicator(s) and prints it (them)
   * @param args
   * @param normalize
   * @throws FileNotFoundException
   */
  private static void calculateAndPrintIndicators(String[] args, boolean normalize)
      throws FileNotFoundException {
    Front referenceFront = new ArrayFront(args[1]);
    Front front = new ArrayFront(args[2]);

    if (normalize) {
      FrontNormalizer frontNormalizer = new FrontNormalizer(referenceFront);
      referenceFront = frontNormalizer.normalize(referenceFront);
      front = frontNormalizer.normalize(front);
      JMetalLogger.logger.info("The fronts are NORMALIZED before computing the indicators"); ;
    } else {
      JMetalLogger.logger.info("The fronts are NOT NORMALIZED before computing the indicators") ;
    }
    List<QualityIndicator<List<PointSolution>, Double>> indicatorList =
        getAvailableIndicators(referenceFront);

    if (!args[0].equals("ALL")) {
      QualityIndicator<List<PointSolution>, Double> indicator = getIndicatorFromName(
          args[0], indicatorList);
      System.out.println(indicator.evaluate(FrontUtils.convertFrontToSolutionList(front)));
    } else {
      for (QualityIndicator<List<PointSolution>, Double> indicator : indicatorList) {
        System.out.println(indicator.getName() + ": " +
            indicator.evaluate(FrontUtils.convertFrontToSolutionList(front)));
      }

      SetCoverage sc = new SetCoverage() ;
      JMetalLogger.logger.info("SC(refPF, front): " + sc.evaluate(
          FrontUtils.convertFrontToSolutionList(referenceFront),
          FrontUtils.convertFrontToSolutionList(front))) ;
      JMetalLogger.logger.info("SC(front, refPF): " + sc.evaluate(
          FrontUtils.convertFrontToSolutionList(front),
          FrontUtils.convertFrontToSolutionList(referenceFront))) ;
    }
  }

  /**
   * Creates a list with the available indicators (but setCoverage)
   * @param referenceFront
   * @return
   * @throws FileNotFoundException
   */
  private static List<QualityIndicator<List<PointSolution>, Double>> getAvailableIndicators(
      Front referenceFront) throws FileNotFoundException {

    List<QualityIndicator<List<PointSolution>, Double>> list = new ArrayList<>() ;
    list.add(new Epsilon<PointSolution>(referenceFront)) ;
    list.add(new PISAHypervolume<PointSolution>(referenceFront)) ;
    list.add(new GenerationalDistance<PointSolution>(referenceFront)) ;
    list.add(new InvertedGenerationalDistance<PointSolution>(referenceFront)) ;
    list.add(new InvertedGenerationalDistancePlus<PointSolution>(referenceFront)) ;
    list.add(new Spread<PointSolution>(referenceFront)) ;
    list.add(new GeneralizedSpread<PointSolution>(referenceFront)) ;
    //list.add(new R2<List<DoubleSolution>>(referenceFront)) ;
    list.add(new ErrorRatio<List<PointSolution>>(referenceFront)) ;

    return list ;
  }

  /**
   * Given an indicator name, finds the indicator in the list of indicator
   * @param name
   * @param list
   * @return
   */
  private static QualityIndicator<List<PointSolution>, Double> getIndicatorFromName(
      String name, List<QualityIndicator<List<PointSolution>, Double>> list) {
    QualityIndicator<List<PointSolution>, Double> result = null ;

    for (QualityIndicator<List<PointSolution>, Double> indicator : list) {
      if (indicator.getName().equals(name)) {
        result = indicator ;
      }
    }

    if (result == null) {
      throw new JMetalException("Indicator " + name + " not available") ;
    }

    return result ;
  }
}
