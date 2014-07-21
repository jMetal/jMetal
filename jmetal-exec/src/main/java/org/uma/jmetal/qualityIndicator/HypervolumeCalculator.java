package org.uma.jmetal.qualityIndicator;

import org.uma.jmetal.qualityIndicator.util.MetricsUtil;
import org.uma.jmetal.util.Configuration;
import org.uma.jmetal.util.JMetalException;

/**
 * Created by Antonio J. Nebro on 21/07/14.
 */
public class HypervolumeCalculator {
  public static void main(String args[]) throws JMetalException {
    if (args.length < 2) {
      throw new JMetalException(
        "Error using Hypervolume. Usage: \n java org.uma.jmetal.qualityIndicator.Hypervolume " +
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

    Configuration.logger.info(""+value);
  }
}
