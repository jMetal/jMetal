package org.uma.jmetal.qualityindicator.impl;

import java.io.FileNotFoundException;
import java.util.Arrays;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements the inverted generational distance metric.
 * Reference: Van Veldhuizen, D.A., Lamont, G.B.: Multiobjective Evolutionary Algorithm Research:
 * A History and Analysis.
 * Technical Report TR-98-03, Dept. Elec. Comput. Eng., Air Force
 * Inst. Technol. (1998)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class InvertedGenerationalDistance extends QualityIndicator {

  private double pow = 2.0;

  /**
   * Default constructor
   */
  public InvertedGenerationalDistance() {
  }

  /**
   * Constructor
   *
   * @param referenceFront
   * @throws FileNotFoundException
   */
  public InvertedGenerationalDistance(double[][] referenceFront) {
    super(referenceFront) ;
  }

  /**
   * Constructor
   *
   * @param referenceFront
   * @throws FileNotFoundException
   */
  public InvertedGenerationalDistance(double[][] referenceFront, double pow) {
    super(referenceFront) ;
    this.pow = pow ;
  }

  /**
   * Evaluate() method
   * @param front
   * @return
   */
  @Override public double compute(double[][] front) {
    Check.notNull(front);
    return invertedGenerationalDistance(front, referenceFront);
  }

  /**
   * Returns the inverted generational distance value for a given front
   *
   * @param front The front
   * @param referenceFront The reference pareto front
   */
  public double invertedGenerationalDistance(double[][] front, double[][] referenceFront) {
    double sum = Arrays.stream(referenceFront).mapToDouble(vector -> Math.pow(VectorUtils.distanceToClosestVector(vector, front), pow)).sum();

      sum = Math.pow(sum, 1.0 / pow);

    return sum / referenceFront.length;
  }

  @Override public String getName() {
    return "IGD" ;
  }

  @Override public String getDescription() {
    return "Inverted generational distance quality indicator" ;
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true ;
  }
}
