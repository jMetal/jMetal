package org.uma.jmetal.qualityIndicator;

import org.uma.jmetal.qualityIndicator.util.MetricsUtil;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.JMetalException;

/**
 * Created by Antonio J. Nebro on 21/07/14.
 */
public class InvertedGenerationalDistanceCalculator {
  public static void main(String args[]) throws JMetalException {
    if (args.length < 2) {
      throw new JMetalException("InvertedGenerationalDistance::Main: Usage: java " +
        "InvertedGenerationalDistance <FrontFile> " + "<TrueFrontFile>");
    }

    MetricsUtil utils = new MetricsUtil() ;

    // STEP 1. Create an instance of Generational Distance
    InvertedGenerationalDistance qualityIndicator = new InvertedGenerationalDistance();

    // STEP 2. Read the fronts from the files
    double[][] solutionFront = utils.readFront(args[0]);
    double[][] trueFront = utils.readFront(args[1]);

    // STEP 3. Obtain the metric value
    double value = qualityIndicator.invertedGenerationalDistance(solutionFront, trueFront);

    JMetalLogger.logger.info(""+value);
  }
}
