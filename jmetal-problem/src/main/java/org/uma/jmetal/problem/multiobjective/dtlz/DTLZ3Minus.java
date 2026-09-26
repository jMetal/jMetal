package org.uma.jmetal.problem.multiobjective.dtlz;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * DTLZ3, negated and rescaled so that the ideal point is the origin and the nadir point is
 * {@code (1, ..., 1)}, following Ishibuchi, Setoguchi, Masuda and Nojima, "Performance of
 * Decomposition-Based Many-Objective Algorithms Strongly Depends on Pareto Front Shapes," IEEE
 * TEVC, 21(2), 169-190, 2017, doi:10.1109/TEVC.2016.2587749 (Eq. 9). Plain negation of the
 * objectives is not enough: it would make solutions with a larger distance-function value {@code
 * g(x_m)} Pareto-dominate solutions with {@code g(x_m) = 0}, moving the optimum to the
 * decision-space boundary instead of leaving it at {@code x_m = 0.5} as in the original DTLZ3. See
 * {@link DTLZMinusTransform} for the rescaling this class applies to restore that property.
 */
@SuppressWarnings("serial")
public class DTLZ3Minus extends DTLZ3 {
  private final double gMax;

  /**
   * Creates a default DTLZ3Minus problem (12 variables and 3 objectives)
   */
  public DTLZ3Minus() {
    this(12, 3);
  }

  /**
   * Creates a DTLZ3Minus problem instance
   *
   * @param numberOfVariables  Number of variables
   * @param numberOfObjectives Number of objective functions
   */
  public DTLZ3Minus(Integer numberOfVariables, Integer numberOfObjectives) {
    super(numberOfVariables, numberOfObjectives);
    name("DTLZ3Minus");
    int k = numberOfVariables - numberOfObjectives + 1;
    gMax = DTLZMinusTransform.multimodalDistanceMaximum(k);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    super.evaluate(solution);
    DTLZMinusTransform.negateSphereFront(solution.objectives(), gMax);
    return solution;
  }
}
