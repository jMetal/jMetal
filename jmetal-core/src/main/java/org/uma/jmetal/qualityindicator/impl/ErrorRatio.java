package org.uma.jmetal.qualityindicator.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * The Error Ratio (ER) quality indicator reports the ratio of solutions in a front of points
 * that are not members of the true Pareto front.
 *
 * NOTE: the indicator merely checks if the solutions in the front are not members of the
 * second front. No assumption is made about the second front is a true Pareto front, i.e,
 * the front could contain solutions that dominate some of those of the supposed Pareto front.
 * It is a responsibility of the caller to ensure that this does not happen.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * TODO: using an epsilon value
 */
@SuppressWarnings("serial")
public class ErrorRatio extends QualityIndicator {
  private double[][] referenceFront ;

  /**
   * Constructor
   */
  public ErrorRatio() {
  }

  /**
   * Constructor
   *
   * @param referenceFrontFile
   * @throws FileNotFoundException
   */
  public ErrorRatio(String referenceFrontFile) throws IOException {
    referenceFront = VectorUtils.readVectors(referenceFrontFile);
  }

  /**
   * Constructor
   *
   * @param referenceFront
   */
  public ErrorRatio(double[][] referenceFront) {
    Check.notNull(referenceFront);
    this.referenceFront = referenceFront ;
  }

  /**
   * Evaluate() method
   * @param front
   * @return
   */
  @Override public double compute(double[][] front) {
    Check.notNull(front);

    return errorRatio(front, referenceFront);
  }

  /**
   * Returns the value of the error ratio indicator.
   *
   * @param front Solution front
   * @param referenceFront True Pareto front
   *
   * @return the value of the error ratio indicator
   * @throws JMetalException
   */
  private double errorRatio(double[][] front, double[][] referenceFront) {
    int numberOfObjectives = referenceFront[0].length ;
    double sum = 0;

    for (double[] currentPoint : front) {
      boolean thePointIsInTheParetoFront = false;
      for (double[] currentParetoFrontPoint : referenceFront) {
        boolean found = true;
        for (int k = 0; k < numberOfObjectives; k++) {
          if (currentPoint[k] != currentParetoFrontPoint[k]) {
            found = false;
            break;
          }
        }
        if (found) {
          thePointIsInTheParetoFront = true;
          break;
        }
      }
      if (!thePointIsInTheParetoFront) {
        sum++;
      }
    }

    return sum / front.length;
  }

  @Override
  public void referenceFront(double[][] referenceFront) {
    this.referenceFront = referenceFront;
  }

  @Override public String getDescription() {
    return "Error ratio" ;
  }

  @Override public String getName() {
    return "ER" ;
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true ;
  }
}
