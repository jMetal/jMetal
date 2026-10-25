package org.uma.jmetal.problem.multiobjective.cec2009;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem CF3 of the CEC 2009 competition on multiobjective optimization: Q. Zhang, A. Zhou, S.
 * Zhao, P. N. Suganthan, W. Liu and S. Tiwari, "Multiobjective optimization test instances for the
 * CEC 2009 special session and competition", Technical Report CES-487, University of Essex and
 * Nanyang Technological University, 2008 (revised on April 20, 2009), constrained problem 3.
 *
 * <pre>
 * f1 = x1       + 2 / |J1| (4 sum_{j in J1} y_j^2 - 2 prod_{j in J1} cos(20 y_j pi / sqrt(j)) + 2)
 * f2 = 1 - x1^2 + 2 / |J2| (4 sum_{j in J2} y_j^2 - 2 prod_{j in J2} cos(20 y_j pi / sqrt(j)) + 2)
 * c  = f2 + f1^2 - a sin(N pi (f1^2 - f2 + 1)) - 1 &gt;= 0,  N = 2, a = 1
 * y_j = x_j - sin(6 pi x1 + j pi / n)
 * J1 = {j odd, 2 &lt;= j &lt;= n},  J2 = {j even, 2 &lt;= j &lt;= n}
 * x in [0, 1] x [-2, 2]^(n-1)
 * </pre>
 *
 * <p>The Pareto front is the isolated point (0, 1) and the parts of f2 = 1 - f1^2 with f1 in
 * [sqrt((2i - 1) / 2N), sqrt(2i / 2N)], i = 1, ..., N, that is, [1/2, sqrt(1/2)] and [sqrt(3/4),
 * 1]; the Pareto set has x1 = f1 and y_j = 0.
 *
 * <p>The definition follows the C code of the competition, which agrees with the report, PlatEMO,
 * pymoo-based codes (cmo) and the MOEA Framework. The report and the code write the constraint as
 * c &gt;= 0, jMetal's convention (a value &gt;= 0 is satisfied); PlatEMO and cmo negate it.
 * The problem needs at least 3 variables (J1 must not be empty).
 */
@SuppressWarnings("serial")
public class CEC2009CF3 extends AbstractDoubleProblem {

  private static final double N = 2.0;
  private static final double A = 1.0;

  /** Creates the problem with 10 variables, as in the competition. */
  public CEC2009CF3() {
    this(10);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 3
   */
  public CEC2009CF3(int numberOfVariables) {
    CEC2009CFFunctions.setUp(this, "CEC2009CF3", numberOfVariables, 3, 2, 1, -2.0, 2.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = CEC2009CFFunctions.variables(solution);
    int n = x.length;
    double sum1 = 0.0;
    double sum2 = 0.0;
    double prod1 = 1.0;
    double prod2 = 1.0;
    int count1 = 0;
    int count2 = 0;
    for (int j = 2; j <= n; j++) {
      double yj = x[j - 1] - Math.sin(6.0 * Math.PI * x[0] + j * Math.PI / n);
      double pj = Math.cos(20.0 * yj * Math.PI / Math.sqrt(j));
      if (j % 2 == 0) {
        sum2 += yj * yj;
        prod2 *= pj;
        count2++;
      } else {
        sum1 += yj * yj;
        prod1 *= pj;
        count1++;
      }
    }
    double f1 = x[0] + 2.0 * (4.0 * sum1 - 2.0 * prod1 + 2.0) / count1;
    double f2 = 1.0 - x[0] * x[0] + 2.0 * (4.0 * sum2 - 2.0 * prod2 + 2.0) / count2;

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] =
        f2 + f1 * f1 - A * Math.sin(N * Math.PI * (f1 * f1 - f2 + 1.0)) - 1.0;
    return solution;
  }
}
