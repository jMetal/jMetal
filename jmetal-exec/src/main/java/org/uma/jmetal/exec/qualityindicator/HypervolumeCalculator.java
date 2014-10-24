package org.uma.jmetal.exec.qualityindicator;

import org.uma.jmetal.qualityindicator.Hypervolume;
import org.uma.jmetal.qualityindicator.util.MetricsUtil;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.JMetalException;

/**
 * Created by Antonio J. Nebro on 21/07/14.
 */
public class HypervolumeCalculator {
  public static void main(String args[]) throws JMetalException {
    if (args.length < 2) {
      throw new JMetalException(
        "Error using Hypervolume. Usage: \n java org.uma.jmetal.qualityindicator.Hypervolume " +
          "<SolutionFrontFile> " +
          "<TrueFrontFile> " + "<getNumberOfObjectives>");
    }

    MetricsUtil util = new MetricsUtil() ;

    //Create a new instance of the metric
    Hypervolume qualityIndicator = new Hypervolume();

    //Read the front from the files
    double[][] solutionFront = util.readFront(args[0]);
    double[][] trueFront = util.readFront(args[1]);

    //Obtain delta value
    double value = qualityIndicator.hypervolume(solutionFront, trueFront);

    JMetalLogger.logger.info(""+value);
  }
}
