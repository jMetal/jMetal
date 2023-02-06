package org.uma.jmetal.qualityindicator;

import org.uma.jmetal.util.errorchecking.Check;

/**
 * Abstract class representing quality indicators. It is assumed that the fronts are normalized
 * before computing the indicators.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public abstract class QualityIndicator {
  protected double[][] referenceFront ;

  protected QualityIndicator() {
  }

  protected QualityIndicator(double[][] referenceFront) {
    Check.notNull(referenceFront);
    this.referenceFront = referenceFront;
  }

  public abstract double compute(double[][] front) ;

  public void referenceFront(double[][] referenceFront) {
    this.referenceFront = referenceFront;
  }

  /**
   * Returns true if lower indicator values are preferred and false otherwise
   */
  public abstract boolean isTheLowerTheIndicatorValueTheBetter();

  public double[][] referenceFront() {
    return referenceFront;
  }

  public abstract String getName() ;
  public abstract String getDescription() ;
}
