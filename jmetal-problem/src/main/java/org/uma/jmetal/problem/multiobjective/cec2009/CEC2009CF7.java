package org.uma.jmetal.problem.multiobjective.cec2009;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem CF7 of the CEC 2009 competition on multiobjective optimization: Q. Zhang, A. Zhou, S.
 * Zhao, P. N. Suganthan, W. Liu and S. Tiwari, "Multiobjective optimization test instances for the
 * CEC 2009 special session and competition", Technical Report CES-487, University of Essex and
 * Nanyang Technological University, 2008 (revised on April 20, 2009), constrained problem 7.
 *
 * <pre>
 * f1 = x1         + sum_{j in J1} h_j(y_j)
 * f2 = (1 - x1)^2 + sum_{j in J2} h_j(y_j)
 * c1 = x2 - sin(6 pi x1 + 2 pi / n) - sign(g1) sqrt(|g1|) &gt;= 0,  g1 = (x1 - 0.5)(1 - x1)
 * c2 = x4 - sin(6 pi x1 + 4 pi / n) - sign(g2) sqrt(|g2|) &gt;= 0,
 *      g2 = 0.25 sqrt(1 - x1) - 0.5 (1 - x1)
 * y_j = x_j - cos(6 pi x1 + j pi / n), j in J1
 * y_j = x_j - sin(6 pi x1 + j pi / n), j in J2
 * h2(t) = h4(t) = t^2,  h_j(t) = 2 t^2 - cos(4 pi t) + 1 for the other j
 * J1 = {j odd, 2 &lt;= j &lt;= n},  J2 = {j even, 2 &lt;= j &lt;= n}
 * x in [0, 1] x [-2, 2]^(n-1)
 * </pre>
 *
 * <p>The constraints are y2 &gt;= sign(g1) sqrt(|g1|) and y4 &gt;= sign(g2) sqrt(|g2|), and the
 * Pareto front is that of CF6: f2 = (1 - f1)^2 for f1 in [0, 0.5], f2 = 0.5 (1 - f1) for f1 in
 * (0.5, 0.75] and f2 = 0.25 sqrt(1 - f1) for f1 in (0.75, 1]. Its Pareto set has x1 = f1, y_j = 0
 * for j other than 2 and 4, y2 = sqrt(g1) for x1 &gt; 0.5 and y4 = sqrt(g2) for x1 &gt; 0.75 (0
 * otherwise).
 *
 * <p>The definition follows the C code of the competition. The report, PlatEMO and cmo write g1 as
 * 0.5 (1 - x1) - (1 - x1)^2, which is the same function. The report and the code write the
 * constraints as c &gt;= 0, jMetal's convention (a value &gt;= 0 is satisfied); PlatEMO and cmo
 * negate them. The problem needs at least 4 variables (x4 is in the second constraint).
 */
@SuppressWarnings("serial")
public class CEC2009CF7 extends AbstractDoubleProblem {

  /** Creates the problem with 10 variables, as in the competition. */
  public CEC2009CF7() {
    this(10);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 4
   */
  public CEC2009CF7(int numberOfVariables) {
    CEC2009CFFunctions.setUp(this, "CEC2009CF7", numberOfVariables, 4, 2, 2, -2.0, 2.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = CEC2009CFFunctions.variables(solution);
    int n = x.length;
    double sum1 = 0.0;
    double sum2 = 0.0;
    for (int j = 2; j <= n; j++) {
      double angle = 6.0 * Math.PI * x[0] + j * Math.PI / n;
      if (j % 2 == 1) {
        double yj = x[j - 1] - Math.cos(angle);
        sum1 += 2.0 * yj * yj - Math.cos(4.0 * Math.PI * yj) + 1.0;
      } else {
        double yj = x[j - 1] - Math.sin(angle);
        sum2 +=
            (j == 2 || j == 4) ? yj * yj : 2.0 * yj * yj - Math.cos(4.0 * Math.PI * yj) + 1.0;
      }
    }
    double g1 = (x[0] - 0.5) * (1.0 - x[0]);
    double g2 = 0.25 * Math.sqrt(1.0 - x[0]) - 0.5 * (1.0 - x[0]);

    solution.objectives()[0] = x[0] + sum1;
    solution.objectives()[1] = (1.0 - x[0]) * (1.0 - x[0]) + sum2;
    solution.constraints()[0] =
        x[1]
            - Math.sin(6.0 * x[0] * Math.PI + 2.0 * Math.PI / n)
            - CEC2009CFFunctions.signedSquareRoot(g1);
    solution.constraints()[1] =
        x[3]
            - Math.sin(6.0 * x[0] * Math.PI + 4.0 * Math.PI / n)
            - CEC2009CFFunctions.signedSquareRoot(g2);
    return solution;
  }
}
