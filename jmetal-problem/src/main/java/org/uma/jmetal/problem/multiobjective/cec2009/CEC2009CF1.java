package org.uma.jmetal.problem.multiobjective.cec2009;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem CF1 of the CEC 2009 competition on multiobjective optimization: Q. Zhang, A. Zhou, S.
 * Zhao, P. N. Suganthan, W. Liu and S. Tiwari, "Multiobjective optimization test instances for the
 * CEC 2009 special session and competition", Technical Report CES-487, University of Essex and
 * Nanyang Technological University, 2008 (revised on April 20, 2009), constrained problem 1.
 *
 * <pre>
 * f1 = x1     + 2 / |J1| sum_{j in J1} (x_j - x1^(0.5 (1 + 3 (j - 2) / (n - 2))))^2
 * f2 = 1 - x1 + 2 / |J2| sum_{j in J2} (x_j - x1^(0.5 (1 + 3 (j - 2) / (n - 2))))^2
 * c  = f1 + f2 - a |sin(N pi (f1 - f2 + 1))| - 1 &gt;= 0,  N = 10, a = 1
 * J1 = {j odd, 2 &lt;= j &lt;= n},  J2 = {j even, 2 &lt;= j &lt;= n}
 * x in [0, 1]^n
 * </pre>
 *
 * <p>The Pareto front is made of the 2N + 1 = 21 points (i / 20, 1 - i / 20), i = 0, ..., 20, which
 * lie on the boundary of the feasible region; the Pareto set has x1 = i / 20 and the other
 * variables at x1^(0.5 (1 + 3 (j - 2) / (n - 2))).
 *
 * <p>The definition follows the C code of the competition, which agrees with the report, PlatEMO,
 * pymoo-based codes (cmo) and the MOEA Framework. The report and the code write the constraint as
 * c &gt;= 0, jMetal's convention (a value &gt;= 0 is satisfied); PlatEMO and cmo negate it.
 * The problem needs at least 3 variables (J1 must not be empty, and n - 2 is a divisor).
 */
@SuppressWarnings("serial")
public class CEC2009CF1 extends AbstractDoubleProblem {

  private static final double N = 10.0;
  private static final double A = 1.0;

  /** Creates the problem with 10 variables, as in the competition. */
  public CEC2009CF1() {
    this(10);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 3
   */
  public CEC2009CF1(int numberOfVariables) {
    CEC2009CFFunctions.setUp(this, "CEC2009CF1", numberOfVariables, 3, 2, 1, 0.0, 1.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = CEC2009CFFunctions.variables(solution);
    int n = x.length;
    double sum1 = 0.0;
    double sum2 = 0.0;
    int count1 = 0;
    int count2 = 0;
    for (int j = 2; j <= n; j++) {
      double yj = x[j - 1] - Math.pow(x[0], 0.5 * (1.0 + 3.0 * (j - 2.0) / (n - 2.0)));
      if (j % 2 == 1) {
        sum1 += yj * yj;
        count1++;
      } else {
        sum2 += yj * yj;
        count2++;
      }
    }
    double f1 = x[0] + 2.0 * sum1 / count1;
    double f2 = 1.0 - x[0] + 2.0 * sum2 / count2;

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] =
        f1 + f2 - A * Math.abs(Math.sin(N * Math.PI * (f1 - f2 + 1.0))) - 1.0;
    return solution;
  }
}
