package org.uma.jmetal.problem.multiobjective.cec2009;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem CF4 of the CEC 2009 competition on multiobjective optimization: Q. Zhang, A. Zhou, S.
 * Zhao, P. N. Suganthan, W. Liu and S. Tiwari, "Multiobjective optimization test instances for the
 * CEC 2009 special session and competition", Technical Report CES-487, University of Essex and
 * Nanyang Technological University, 2008 (revised on April 20, 2009), constrained problem 4.
 *
 * <pre>
 * f1 = x1     + sum_{j in J1} h_j(y_j)
 * f2 = 1 - x1 + sum_{j in J2} h_j(y_j)
 * c  = t / (1 + e^(4 |t|)) &gt;= 0,  t = x2 - sin(6 pi x1 + 2 pi / n) - 0.5 x1 + 0.25
 * y_j = x_j - sin(6 pi x1 + j pi / n)
 * h2(t) = |t| if t &lt; 3/2 (1 - sqrt(2)/2), 0.125 + (t - 1)^2 otherwise;  h_j(t) = t^2, j &gt;= 3
 * J1 = {j odd, 2 &lt;= j &lt;= n},  J2 = {j even, 2 &lt;= j &lt;= n}
 * x in [0, 1] x [-2, 2]^(n-1)
 * </pre>
 *
 * <p>The constraint is y2 &gt;= 0.5 x1 - 0.25. The Pareto front is f2 = 1 - f1 for f1 in [0,
 * 0.5], f2 = -0.5 f1 + 3/4 for f1 in (0.5, 0.75] and f2 = 1 - f1 + 0.125 for f1 in (0.75, 1]. Its
 * Pareto set has x1 = f1, y_j = 0 for j &gt;= 3 and y2 = 0 (first part), y2 = 0.5 x1 - 0.25 (second
 * part, on the boundary of the feasible region) or y2 = 1, the local minimum of h2 (third part).
 * The objectives add h_j(y_j), not its mean as in CF1-CF3.
 *
 * <p>The definition follows the C code of the competition, which agrees with the report, PlatEMO,
 * pymoo-based codes (cmo) and the MOEA Framework, also in the threshold of h2, 1.5 - 0.75 sqrt(2).
 * The report and the code write the constraint as c &gt;= 0, jMetal's convention (a value &gt;= 0
 * is satisfied); PlatEMO and cmo negate it. The problem needs at least 2 variables (x2 is in the
 * constraint); cmo asks for 3.
 */
@SuppressWarnings("serial")
public class CEC2009CF4 extends AbstractDoubleProblem {

  /** Creates the problem with 10 variables, as in the competition. */
  public CEC2009CF4() {
    this(10);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public CEC2009CF4(int numberOfVariables) {
    CEC2009CFFunctions.setUp(this, "CEC2009CF4", numberOfVariables, 2, 2, 1, -2.0, 2.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = CEC2009CFFunctions.variables(solution);
    int n = x.length;
    double sum1 = 0.0;
    double sum2 = 0.0;
    for (int j = 2; j <= n; j++) {
      double yj = x[j - 1] - Math.sin(6.0 * Math.PI * x[0] + j * Math.PI / n);
      if (j % 2 == 1) {
        sum1 += yj * yj;
      } else if (j == 2) {
        sum2 += CEC2009CFFunctions.h2(yj);
      } else {
        sum2 += yj * yj;
      }
    }
    double t = x[1] - Math.sin(6.0 * x[0] * Math.PI + 2.0 * Math.PI / n) - 0.5 * x[0] + 0.25;

    solution.objectives()[0] = x[0] + sum1;
    solution.objectives()[1] = 1.0 - x[0] + sum2;
    solution.constraints()[0] = CEC2009CFFunctions.sigmoid(t);
    return solution;
  }
}
