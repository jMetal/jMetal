package org.uma.jmetal.experimental.qualityIndicator.impl;

import org.uma.jmetal.experimental.qualityIndicator.QualityIndicator;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.front.util.FrontUtils;

import java.io.FileNotFoundException;

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
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public InvertedGenerationalDistance(String referenceParetoFrontFile, double p) {
    super(referenceParetoFrontFile) ;
    pow = p ;
  }

  /**
   * Constructor
   *
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public InvertedGenerationalDistance(String referenceParetoFrontFile) {
    this(referenceParetoFrontFile, 2.0) ;
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
   * Evaluate() method
   * @param front
   * @return
   */
  @Override public double compute(double[][] front) {
    Check.isNotNull(front);
    return invertedGenerationalDistance(front, referenceFront);
  }

  /**
   * Returns the inverted generational distance value for a given front
   *
   * @param front The front
   * @param referenceFront The reference pareto front
   */
  public double invertedGenerationalDistance(double[][] front, double[][] referenceFront) {
    double sum = 0.0;
    for (double[] vector : referenceFront) {
      sum += Math.pow(VectorUtils.distanceToClosestVector(vector, front), pow);
    }

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
