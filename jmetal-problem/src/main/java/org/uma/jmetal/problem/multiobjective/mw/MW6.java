package org.uma.jmetal.problem.multiobjective.mw;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MW6 of Z. Ma and Y. Wang, "Evolutionary Constrained Multiobjective Optimization: Test
 * Suite Construction and Performance Comparisons", IEEE Transactions on Evolutionary Computation
 * 23(6):972-986, 2019 (https://doi.org/10.1109/TEVC.2019.2896967), Eq. 20. Its Pareto front is made
 * of disconnected arcs of the unconstrained one, the quarter of the circle of radius 1.1 (Type II).
 *
 * <pre>
 * f1 = g2 x1
 * f2 = g2 sqrt(1.1^2 - (f1 / g2)^2)
 * c  = 1 - (f1 / (1 + 0.15 l))^2 - (f2 / (1 + 0.75 l))^2 &gt;= 0,  l = cos(6 arctan(f2 / f1)^4)^10
 * g2 = 1 + sum_{i=2..n} (1.5 + 0.1 / n z_i^2 - 1.5 cos(2 pi z_i)),  z_i = 1 - exp(-10 (x_i - (i - 1) / n)^2)
 * x in [0, 1.1]^n
 * </pre>
 *
 * <p>The constraint follows jMetal's convention (a value &gt;= 0 is satisfied). pymoo follows the
 * paper. PlatEMO takes x in [0, 1]^n and f1 = 1.0999 g2 x1, so its f1 does not reach 1.1 and it
 * misses the end of the front between (1.0999, 0.0148) and (1.1, 0). The square root takes max(0,
 * 1.1^2 - (f1 / g2)^2), which is negative only by rounding.
 */
@SuppressWarnings("serial")
public class MW6 extends AbstractDoubleProblem {

  /** Creates the problem with 15 variables. */
  public MW6() {
    this(15);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public MW6(int numberOfVariables) {
    MWFunctions.setUp(this, "MW6", numberOfVariables, 2, 1, 1.1);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = MWFunctions.variables(solution);
    double g = MWFunctions.g2(x, 2);
    double f1 = g * x[0];
    double f2 = g * MWFunctions.sqrtOfNonNegative(1.1 * 1.1 - (f1 / g) * (f1 / g));
    double l = Math.pow(Math.cos(6.0 * Math.pow(Math.atan(f2 / f1), 4)), 10);
    double a = f1 / (1.0 + 0.15 * l);
    double b = f2 / (1.0 + 0.75 * l);

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] = 1.0 - a * a - b * b;
    return solution;
  }
}
