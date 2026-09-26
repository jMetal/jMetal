package org.uma.jmetal.problem.multiobjective.dtlz;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * DTLZ1, negated and rescaled so that the ideal point is the origin and the nadir point is
 * {@code (1, ..., 1)}, following Ishibuchi, Setoguchi, Masuda and Nojima, "Performance of
 * Decomposition-Based Many-Objective Algorithms Strongly Depends on Pareto Front Shapes," IEEE
 * TEVC, 21(2), 169-190, 2017, doi:10.1109/TEVC.2016.2587749 (Eq. 9). Plain negation of the
 * objectives is not enough: it would make solutions with a larger distance-function value {@code
 * g(x_m)} Pareto-dominate solutions with {@code g(x_m) = 0}, moving the optimum to the
 * decision-space boundary instead of leaving it at {@code x_m = 0.5} as in the original DTLZ1. See
 * {@link DTLZMinusTransform} for the rescaling this class applies to restore that property.
 */
@SuppressWarnings("serial")
public class DTLZ1Minus extends DTLZ1 {
  private final double gMax;

  /**
   * Creates a default DTLZ1Minus problem (7 variables and 3 objectives)
   */
  public DTLZ1Minus() {
    this(7, 3);
  }

  /**
   * Creates a DTLZ1Minus problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public DTLZ1Minus(Integer numberOfVariables, Integer numberOfObjectives) throws JMetalException {
    super(numberOfVariables, numberOfObjectives);
    name("DTLZ1Minus");
    int k = numberOfVariables - numberOfObjectives + 1;
    gMax = DTLZMinusTransform.multimodalDistanceMaximum(k);
  }

  /** Evaluate() method */
  public DoubleSolution evaluate(DoubleSolution solution) {
    super.evaluate(solution);
    DTLZMinusTransform.negateSumFront(solution.objectives(), 0.5, gMax);
    return solution;
  }
}
