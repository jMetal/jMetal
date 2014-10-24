package org.uma.jmetal.exec.qualityindicator;

import org.uma.jmetal.qualityindicator.Spread;
import org.uma.jmetal.qualityindicator.util.MetricsUtil;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.JMetalException;

/**
 * Created by Antonio J. Nebro on 21/07/14.
 */
public class SpreadCalculator {
  public static void main(String args[]) throws JMetalException {
    if (args.length < 2) {
      throw new JMetalException("Spread::Main: Error using Spread. Usage: \n java " +
        "Spread <FrontFile> <TrueFrontFile>  ");
    }

    MetricsUtil utils = new MetricsUtil() ;

    // STEP 1. Create a new instance of the metric
    Spread qualityIndicator = new Spread();

    // STEP 2. Read the fronts from the files
    double[][] solutionFront = utils.readFront(args[0]);
    double[][] trueFront = utils.readFront(args[1]);

    // STEP 3. Obtain the metric value
    double value = qualityIndicator.spread(solutionFront, trueFront);

    JMetalLogger.logger.info("" + value);
  }

}
