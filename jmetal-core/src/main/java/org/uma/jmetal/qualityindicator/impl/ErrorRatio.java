package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;

import java.io.FileNotFoundException;
import java.io.IOException;

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
    Check.isNotNull(referenceFront);
    this.referenceFront = referenceFront ;
  }

  /**
   * Evaluate() method
   * @param front
   * @return
   */
  @Override public double compute(double[][] front) {
    Check.isNotNull(front);

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

    for (int i = 0; i < front.length; i++) {
      double[] currentPoint = front[i];
      boolean thePointIsInTheParetoFront = false;
      for (int j = 0; j < referenceFront.length; j++) {
        double[] currentParetoFrontPoint = referenceFront[j];
        boolean found = true;
        for (int k = 0; k < numberOfObjectives; k++) {
          if(currentPoint[k] != currentParetoFrontPoint[k]){
            found = false;
            break;
          }
        }
        if(found){
          thePointIsInTheParetoFront = true;
          break;
        }
      }
      if(!thePointIsInTheParetoFront){
        sum++;
      }
    }

    return sum / front.length;
  }

  public void setReferenceFront(double[][] referenceFront) {
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
