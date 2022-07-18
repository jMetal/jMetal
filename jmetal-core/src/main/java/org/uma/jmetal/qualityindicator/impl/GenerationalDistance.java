package org.uma.jmetal.qualityindicator.impl;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.Arrays;

/**
 * This class implements the generational distance indicator.
 * Reference: Van Veldhuizen, D.A., Lamont, G.B.: Multiobjective Evolutionary
 * Algorithm Research: A History and Analysis.
 * Technical Report TR-98-03, Dept. Elec. Comput. Eng., Air Force
 * Inst. Technol. (1998)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class GenerationalDistance extends QualityIndicator {
  private double pow = 2.0;

  /**
   * Default constructor
   */
  public GenerationalDistance() {
  }

  /**
   * Constructor
   *
   * @param referenceFront
   */
  public GenerationalDistance(double[][] referenceFront) {
    super(referenceFront) ;
  }

  /**
   * Evaluate() method
   * @param front
   * @return
   */
  @Override public double compute(double[] @NotNull [] front) {
    Check.notNull(front);

    return generationalDistance(front, referenceFront);
  }

  /**
   * Returns the generational distance value for a given front
   *
   * @param front           The front
   * @param referenceFront The reference pareto front
   */
  public double generationalDistance(double[] @NotNull [] front, double[][] referenceFront) {
    var sum = 0.0;
      for (var doubles : front) {
        var v = Math.pow(VectorUtils.distanceToClosestVector(doubles, referenceFront), pow);
          sum += v;
      }

      sum = Math.pow(sum, 1.0 / pow);

    return sum / front.length;
  }

  @Override public String getName() {
    return "GD" ;
  }

  @Override
  public @NotNull String getDescription() {
    return "Generational distance quality indicator" ;
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true ;
  }
}
