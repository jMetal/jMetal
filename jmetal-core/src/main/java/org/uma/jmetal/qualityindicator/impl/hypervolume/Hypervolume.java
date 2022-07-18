package org.uma.jmetal.qualityindicator.impl.hypervolume;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.qualityindicator.QualityIndicator;

/**
 * This interface represents implementations of the Hypervolume quality indicator
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public abstract class Hypervolume extends QualityIndicator {

  public Hypervolume() {}

  public Hypervolume(double[][] referenceFront) {
    super(referenceFront) ;
  }

  public Hypervolume(double[] referencePoint) {
    double[] @NotNull [] referenceFront = new double[referencePoint.length][referencePoint.length];
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
  public @NotNull String getName() {
    return "HV";
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return false;
  }
}
