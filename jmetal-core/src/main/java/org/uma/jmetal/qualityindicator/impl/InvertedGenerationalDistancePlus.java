package org.uma.jmetal.qualityindicator.impl;

import java.io.FileNotFoundException;
import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.distance.impl.DominanceDistanceBetweenVectors;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements the inverted generational distance metric plust (IGD+)
 * Reference: Ishibuchi et al 2015, "A Study on Performance Evaluation Ability of a Modified
 * Inverted Generational Distance Indicator", GECCO 2015
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class InvertedGenerationalDistancePlus extends QualityIndicator {

  /**
   * Default constructor
   */
  public InvertedGenerationalDistancePlus() {
  }

  /**
   * Constructor
   *
   * @param referenceFront
   * @throws FileNotFoundException
   */
  public InvertedGenerationalDistancePlus(double[][] referenceFront) {
    super(referenceFront) ;
  }

  /**
   * Evaluate() method
   * @param front
   * @return
   */
  @Override public double compute(double[][] front) {
    Check.notNull(front);

    return invertedGenerationalDistancePlus(front, referenceFront);
  }

  /**
   * Returns the inverted generational distance plus value for a given front
   *
   * @param front The front
   * @param referenceFront The reference pareto front
   */
  public double invertedGenerationalDistancePlus(double[][] front, double[][] referenceFront) {

    var sum = 0.0;
      for (var doubles : referenceFront) {
        var v = VectorUtils.distanceToClosestVector(doubles, front, new DominanceDistanceBetweenVectors());
          sum += v;
      }

      // STEP 4. Divide the sum by the maximum number of points of the reference Pareto front
    return sum / referenceFront.length;
  }

  @Override public @NotNull String getName() {
    return "IGD+" ;
  }

  @Override public @NotNull String getDescription() {
    return "Inverted Generational Distance+" ;
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true ;
  }
}
