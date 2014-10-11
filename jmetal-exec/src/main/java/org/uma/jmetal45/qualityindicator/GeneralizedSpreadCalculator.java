package org.uma.jmetal45.qualityindicator;

import org.uma.jmetal45.qualityindicator.util.MetricsUtil;
import org.uma.jmetal45.util.JMetalException;

/**
 * Created by Antonio J. Nebro on 21/07/14.
 */
public class GeneralizedSpreadCalculator {
  public static void main(String args[]) throws JMetalException {
    if (args.length < 2) {
      throw new JMetalException("Error using GeneralizedSpread. " +
        "Usage: \n java GeneralizedSpread" +
        " <SolutionFrontFile> " + " <TrueFrontFile>") ;
    }

    MetricsUtil utils = new MetricsUtil() ;

    //Create a new instance of the metric
    GeneralizedSpread qualityIndicator = new GeneralizedSpread();

    //Read the front from the files
    double [][] solutionFront = utils.readFront(args[0]);
    double [][] trueFront     = utils.readFront(args[1]);

    //Obtain delta value
    double value = qualityIndicator.generalizedSpread(solutionFront, trueFront);

    System.out.println(value);
  }
}
