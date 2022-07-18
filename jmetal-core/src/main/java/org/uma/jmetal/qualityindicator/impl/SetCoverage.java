package org.uma.jmetal.qualityindicator.impl;

import java.io.FileNotFoundException;
import java.util.Arrays;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Set coverage metric
 *
 * @author Antonio J. Nebro
 * @version 1.0
 */
@SuppressWarnings("serial")
public class SetCoverage extends QualityIndicator {

  /**
   * Constructor
   */
  public SetCoverage() {
  }

  /**
   * Constructor
   *
   * @param referenceFront
   * @throws FileNotFoundException
   */
  public SetCoverage(double[][] referenceFront) {
    super(referenceFront) ;
  }

  @Override
  public double compute(double[] @NotNull [] front) {
    Check.notNull(front);

    return compute(front, referenceFront);
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return false;
  }

  /**
   * Calculates the set coverage of a front over a reference front
   * @param front
   * @param referenceFront
   * @return The value of the set coverage
   */
  public double compute(double[][] front, double[] @NotNull [] referenceFront) {
    Check.notNull(front);
    Check.notNull(referenceFront);

    double result ;
    int sum;

    if (referenceFront.length == 0) {
      if (front.length ==0) {
        result = 0.0 ;
      } else {
        result = 1.0 ;
      }
    } else {
        long count = 0L;
        for (double @NotNull [] vector : referenceFront) {
            if (VectorUtils.isVectorDominatedByAFront(vector, front)) {
                count++;
            }
        }
        sum = (int) count;
      result = (double)sum/referenceFront.length ;
    }
    return result ;
  }

  @Override public String getName() {
    return "SC";
  }

  @Override public String getDescription() {
    return "Set coverage";
  }
}
