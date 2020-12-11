package org.uma.jmetal.qualityIndicator;

import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 *
 */
public abstract class QualityIndicator {
  protected double[][] referenceFront = null;

  /**
   * Default constructor
   */
  public QualityIndicator() {
  }

  /**
   * Constructor
   * @param referenceFrontFile
   */
  public QualityIndicator(String referenceFrontFile) {
    setReferenceFront(referenceFrontFile);
  }

  /**
   * Constructor
   * @param referenceFront
   */
  public QualityIndicator(double[][] referenceFront) {
    this.referenceFront = referenceFront;
  }

  public abstract double compute(double[][] front) ;

  public void setReferenceFront(String referenceFrontFile) {
    Check.isNotNull(referenceFrontFile);

    referenceFront = VectorUtils.readVectors(referenceFrontFile);
  }

  public void setReferenceFront(double[][] referenceFront) {
    this.referenceFront = referenceFront;
  }

  /**
   * This method returns true if lower indicator values are preferred and false otherwise
   *
   * @return
   */
  public abstract boolean isTheLowerTheIndicatorValueTheBetter();

  public double[][] getReferenceFront() {
    return referenceFront;
  }

  public abstract String getName() ;
  public abstract String getDescription() ;
}
