package org.uma.jmetal.problem.multiobjective.cec2009;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem CF10 of the CEC 2009 competition on multiobjective optimization: Q. Zhang, A. Zhou, S.
 * Zhao, P. N. Suganthan, W. Liu and S. Tiwari, "Multiobjective optimization test instances for the
 * CEC 2009 special session and competition", Technical Report CES-487, University of Essex and
 * Nanyang Technological University, 2008 (revised on April 20, 2009), constrained problem 10.
 *
 * <pre>
 * f1 = cos(0.5 x1 pi) cos(0.5 x2 pi) + 2 / |J1| sum_{j in J1} (4 y_j^2 - cos(8 pi y_j) + 1)
 * f2 = cos(0.5 x1 pi) sin(0.5 x2 pi) + 2 / |J2| sum_{j in J2} (4 y_j^2 - cos(8 pi y_j) + 1)
 * f3 = sin(0.5 x1 pi)                + 2 / |J3| sum_{j in J3} (4 y_j^2 - cos(8 pi y_j) + 1)
 * c  = (f1^2 + f2^2) / (1 - f3^2) - a sin(N pi ((f1^2 - f2^2) / (1 - f3^2) + 1)) - 1 &gt;= 0,
 *      N = 2, a = 1
 * y_j = x_j - 2 x2 sin(2 pi x1 + j pi / n)
 * J1 = {3 &lt;= j &lt;= n, j - 1 multiple of 3},  J2 = {3 &lt;= j &lt;= n, j - 2 multiple of 3},
 * J3 = {3 &lt;= j &lt;= n, j multiple of 3}
 * x in [0, 1]^2 x [-2, 2]^(n-2)
 * </pre>
 *
 * <p>The Pareto front is that of CF9: the curve f1 = 0 and the N = 2 surfaces
 * sqrt((2i - 1) / 2N (1 - f3^2)) &lt;= f1 &lt;= sqrt(2i / 2N (1 - f3^2)), f2 = sqrt(1 - f1^2 -
 * f3^2), i = 1, ..., N, of the unit sphere. Its Pareto set has y_j = 0 and x2 in [0, 1/3], [1/2,
 * 2/3] or {1}.
 *
 * <p>The definition follows the C code of the competition, which agrees with PlatEMO, pymoo-based
 * codes (cmo), pagmo2 and the MOEA Framework; the report sums over J1 in the three objectives, a
 * typo (it uses J1, J2 and J3 in CF8 and CF9), and the copy of the C code in the R package emoa
 * (disabled there) ends the constraint with + 1 instead of - 1. The report and the code write the
 * constraint as c &gt;= 0, which is jMetal's convention (a value &gt;= 0 is satisfied); PlatEMO and
 * cmo negate it. Where f3 = 1 the code divides by zero and returns NaN; here the constraint is then
 * 0 (satisfied), which only happens at the top (0, 0, 1) of the front and, by rounding, next to it.
 * The problem needs at least 5 variables (J1, J2 and J3 must not be empty).
 */
@SuppressWarnings("serial")
public class CEC2009CF10 extends AbstractDoubleProblem {

  private static final double N = 2.0;
  private static final double A = 1.0;

  /** Creates the problem with 10 variables, as in the competition. */
  public CEC2009CF10() {
    this(10);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 5
   */
  public CEC2009CF10(int numberOfVariables) {
    CEC2009CFFunctions.setUp(this, "CEC2009CF10", numberOfVariables, 5, 3, 1, -2.0, 2.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] f = solution.objectives();
    CEC2009CFFunctions.sphereObjectives(CEC2009CFFunctions.variables(solution), true, f);
    solution.constraints()[0] = CEC2009CFFunctions.sphereConstraint(f, N, A, false);
    return solution;
  }
}
