package org.uma.jmetal.experimental.qualityIndicator.impl.hypervolume;

import org.uma.jmetal.experimental.qualityIndicator.QualityIndicator;

/**
 * This interface represents implementations of the Hypervolume quality indicator
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public abstract class Hypervolume extends QualityIndicator {

  public Hypervolume() {}

  public Hypervolume(String referenceParetoFrontFile) {
    super(referenceParetoFrontFile);
  }

  public Hypervolume(double[][] referenceFront) {
    super(referenceFront) ;
  }

  public Hypervolume(double[] referencePoint) {
    double[][] referenceFront = new double[referencePoint.length][referencePoint.length];
    for (int i = 0; i < referencePoint.length; i++) {
      double[] point = new double[referencePoint.length] ;
      for (int j = 0; j < referencePoint.length; j++) {
        if (j == i) {
          double v = referencePoint[i] ;
          point[j] = v ;
        } else {
          point[j] = 0.0 ;
        }
      }
      referenceFront[i] = point ;
    }
    this.referenceFront = referenceFront;
  }

  @Override
  public String getName() {
    return "HV";
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return false;
  }
}
