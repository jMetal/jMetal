package org.uma.jmetal.problem.multiobjective.mw;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MW5 of Z. Ma and Y. Wang, "Evolutionary Constrained Multiobjective Optimization: Test
 * Suite Construction and Performance Comparisons", IEEE Transactions on Evolutionary Computation
 * 23(6):972-986, 2019 (https://doi.org/10.1109/TEVC.2019.2896967), Eq. 19. Its Pareto front lies
 * on the unconstrained one, the quarter of the unit circle, and is almost discrete (Type II): the
 * 16 points where sin(6 l2^3) = 0, the two ends (1, 0) and (0, 1) included, which the ends extend
 * into two short arcs (up to an angle of about 0.0139 from the axes), because the constraints are
 * flat (of third order in l2) there.
 *
 * <pre>
 * f1 = g1 x1
 * f2 = g1 sqrt(1 - (f1 / g1)^2)
 * c1 = (1.7 - 0.2 sin(2 l1))^2 - f1^2 - f2^2 &gt;= 0
 * c2 = f1^2 + f2^2 - (1 + 0.5 sin(6 l2^3))^2 &gt;= 0
 * c3 = f1^2 + f2^2 - (1 - 0.45 sin(6 l2^3))^2 &gt;= 0
 * l1 = arctan(f2 / f1),  l2 = 0.5 pi - 2 |arctan(f2 / f1) - 0.25 pi|
 * g1 = 1 + sum_{i=2..n} (1 - exp(-10 (x_i^(n-2) - 0.5 - (i - 1) / (2 n))^2))
 * x in [0, 1]^n
 * </pre>
 *
 * <p>The constraints follow jMetal's convention (a value &gt;= 0 is satisfied): c2 and c3 are the
 * paper's (1 + 0.5 sin(6 l2^3))^2 - f1^2 - f2^2 &lt;= 0 and (1 - 0.45 sin(6 l2^3))^2 - f1^2 - f2^2
 * &lt;= 0 with their signs reversed. PlatEMO and pymoo define the problem in the same way. The
 * square root takes max(0, 1 - (f1 / g1)^2), which is negative only by rounding.
 */
@SuppressWarnings("serial")
public class MW5 extends AbstractDoubleProblem {

  /** Creates the problem with 15 variables. */
  public MW5() {
    this(15);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public MW5(int numberOfVariables) {
    MWFunctions.setUp(this, "MW5", numberOfVariables, 2, 3, 1.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = MWFunctions.variables(solution);
    double g = MWFunctions.g1(x, 2);
    double f1 = g * x[0];
    double f2 = g * MWFunctions.sqrtOfNonNegative(1.0 - (f1 / g) * (f1 / g));
    double l1 = Math.atan(f2 / f1);
    double l2 = 0.5 * Math.PI - 2.0 * Math.abs(l1 - 0.25 * Math.PI);
    double squaredNorm = f1 * f1 + f2 * f2;
    double a = 1.7 - 0.2 * Math.sin(2.0 * l1);
    double b = 1.0 + 0.5 * Math.sin(6.0 * l2 * l2 * l2);
    double c = 1.0 - 0.45 * Math.sin(6.0 * l2 * l2 * l2);

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] = a * a - squaredNorm;
    solution.constraints()[1] = squaredNorm - b * b;
    solution.constraints()[2] = squaredNorm - c * c;
    return solution;
  }
}
