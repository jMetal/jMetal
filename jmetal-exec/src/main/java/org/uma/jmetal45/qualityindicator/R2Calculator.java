package org.uma.jmetal45.qualityindicator;

import org.uma.jmetal45.qualityindicator.util.MetricsUtil;
import org.uma.jmetal45.util.JMetalLogger;
import org.uma.jmetal45.util.JMetalException;

/**
 * Created by Antonio J. Nebro on 21/07/14.
 */
public class R2Calculator {
  /**
   * This class can be call from the command line. At least three parameters are
   * required: 1) the name of the file containing the front, 2) the number of
   * objectives 2) a file containing the reference point / the Optimal Pareto
   * front for normalizing 3) the file containing the weight vector
   *
   * @throws org.uma.jmetal45.util.JMetalException
   */
  public static void main(String args[]) throws JMetalException {
    if (args.length < 3) {
      throw new JMetalException(
        "Error using R2. Usage: \n java org.uma.jmetal45.qualityindicator.R2 "
          + "<SolutionFrontFile> " + "<TrueFrontFile> "
          + "<getNumberOfObjectives>");
    }

    // Create a new instance of the metric
    R2 qualityIndicator;
    // Read the front from the files
    int nObj = new Integer(args[2]);

    if (nObj == 2 && args.length == 3) {
      qualityIndicator = new R2();

    } else {
      qualityIndicator = new R2(nObj, args[3]);
    }

    MetricsUtil utils = new MetricsUtil() ;

    double[][] approximationFront = utils.readFront(args[0]);
    double[][] paretoFront = utils.readFront(args[1]);

    // Obtain delta value
    double value = qualityIndicator.r2(approximationFront, paretoFront);

    JMetalLogger.logger.info(""+value);
    JMetalLogger.logger.info(""+qualityIndicator.R2Without(approximationFront,
      paretoFront, 1));
    JMetalLogger.logger.info(""+qualityIndicator.R2Without(approximationFront,
      paretoFront, 15));
    JMetalLogger.logger.info(""+qualityIndicator.R2Without(approximationFront,
      paretoFront, 25));
    JMetalLogger.logger.info(""+qualityIndicator.R2Without(approximationFront,
      paretoFront, 75));

  }
}
