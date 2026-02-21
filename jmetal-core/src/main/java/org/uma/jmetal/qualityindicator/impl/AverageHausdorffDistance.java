package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements the Average Hausdorff Distance indicator.
 * Reference: Schutze, O., Esquivel, X., Lara, A., & Coello Coello, C. A.
 * (2012).
 * Using the Averaged Hausdorff Distance as a Performance Measure in
 * Evolutionary Multiobjective Optimization.
 * IEEE Transactions on Evolutionary Computation, 16(4), 504–522.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class AverageHausdorffDistance extends QualityIndicator {

  /**
   * Default constructor
   */
  public AverageHausdorffDistance() {
  }

  /**
   * Constructor
   *
   * @param referenceFront
   */
  public AverageHausdorffDistance(double[][] referenceFront) {
    super(referenceFront);
  }

  @Override
  public QualityIndicator newInstance() {
    return new AverageHausdorffDistance();
  }

  /**
   * Evaluate() method
   * 
   * @param front
   * @return
   */
  @Override
  public double compute(double[][] front) {
    Check.notNull(front);

    return averageHausdorffDistance(front, referenceFront);
  }

  /**
   * Returns the value of the average hausdorff distance for a given front
   *
   * @param front          The front
   * @param referenceFront The reference pareto front
   */
  public double averageHausdorffDistance(double[][] front, double[][] referenceFront) {
    double sumGD = 0.0;
    double sumIGD = 0.0;

    for (int i = 0; i < front.length; i++) {
      sumGD += VectorUtils.distanceToClosestVector(front[i], referenceFront);
    }
    double valueGD = sumGD / front.length;

    for (int i = 0; i < referenceFront.length; i++) {
      sumIGD += VectorUtils.distanceToClosestVector(referenceFront[i], front);
    }
    double valueIGD = sumIGD / referenceFront.length;

    return Math.max(valueGD, valueIGD);
  }

  @Override
  public String name() {
    return "AHD";
  }

  @Override
  public String description() {
    return "Average Hausdorff Distance quality indicator";
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true;
  }
}
