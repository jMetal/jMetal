package org.uma.jmetal.problem.multiobjective.cec2009;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem CF2 of the CEC 2009 competition on multiobjective optimization: Q. Zhang, A. Zhou, S.
 * Zhao, P. N. Suganthan, W. Liu and S. Tiwari, "Multiobjective optimization test instances for the
 * CEC 2009 special session and competition", Technical Report CES-487, University of Essex and
 * Nanyang Technological University, 2008 (revised on April 20, 2009), constrained problem 2.
 *
 * <pre>
 * f1 = x1           + 2 / |J1| sum_{j in J1} (x_j - sin(6 pi x1 + j pi / n))^2
 * f2 = 1 - sqrt(x1) + 2 / |J2| sum_{j in J2} (x_j - cos(6 pi x1 + j pi / n))^2
 * c  = t / (1 + e^(4 |t|)) &gt;= 0,  t = f2 + sqrt(f1) - a sin(N pi (sqrt(f1) - f2 + 1)) - 1,
 *      N = 2, a = 1
 * J1 = {j odd, 2 &lt;= j &lt;= n},  J2 = {j even, 2 &lt;= j &lt;= n}
 * x in [0, 1] x [-1, 1]^(n-1)
 * </pre>
 *
 * <p>The Pareto front is the isolated point (0, 1) and the parts of f2 = 1 - sqrt(f1) with f1 in
 * [((2i - 1) / 2N)^2, (2i / 2N)^2], i = 1, ..., N, that is, [1/16, 1/4] and [9/16, 1]; the Pareto
 * set has x1 = f1 and the other variables at sin(6 pi x1 + j pi / n) (odd j) or cos(6 pi x1 + j pi
 * / n) (even j).
 *
 * <p>The definition follows the C code of the competition, which agrees with the report, PlatEMO,
 * pymoo-based codes (cmo) and the MOEA Framework. The report and the code write the constraint as
 * c &gt;= 0, jMetal's convention (a value &gt;= 0 is satisfied); PlatEMO and cmo negate it.
 * The problem needs at least 3 variables (J1 must not be empty).
 */
@SuppressWarnings("serial")
public class CEC2009CF2 extends AbstractDoubleProblem {

  private static final double N = 2.0;
  private static final double A = 1.0;

  /** Creates the problem with 10 variables, as in the competition. */
  public CEC2009CF2() {
    this(10);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 3
   */
  public CEC2009CF2(int numberOfVariables) {
    CEC2009CFFunctions.setUp(this, "CEC2009CF2", numberOfVariables, 3, 2, 1, -1.0, 1.0);
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
      double angle = 6.0 * Math.PI * x[0] + j * Math.PI / n;
      if (j % 2 == 1) {
        double yj = x[j - 1] - Math.sin(angle);
        sum1 += yj * yj;
        count1++;
      } else {
        double yj = x[j - 1] - Math.cos(angle);
        sum2 += yj * yj;
        count2++;
      }
    }
    double f1 = x[0] + 2.0 * sum1 / count1;
    double f2 = 1.0 - Math.sqrt(x[0]) + 2.0 * sum2 / count2;
    double t = f2 + Math.sqrt(f1) - A * Math.sin(N * Math.PI * (Math.sqrt(f1) - f2 + 1.0)) - 1.0;

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] = CEC2009CFFunctions.sigmoid(t);
    return solution;
  }
}
