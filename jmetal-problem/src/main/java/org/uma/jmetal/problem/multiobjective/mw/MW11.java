package org.uma.jmetal.problem.multiobjective.mw;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MW11 of Z. Ma and Y. Wang, "Evolutionary Constrained Multiobjective Optimization: Test
 * Suite Construction and Performance Comparisons", IEEE Transactions on Evolutionary Computation
 * 23(6):972-986, 2019 (https://doi.org/10.1109/TEVC.2019.2896967). The unconstrained Pareto front,
 * the quarter of the circle of radius sqrt(2), is feasible only at the isolated point (1, 1), where
 * c1 = c3 = 0, and the rest of the Pareto front lies on the boundary of the feasible region, in two
 * pieces on both sides of that point (Type IV).
 *
 * <pre>
 * f1 = g3 x1
 * f2 = g3 sqrt(2 - (f1 / g3)^2)
 * c1 = (3 - f1^2 - f2) (3 - 2 f1^2 - f2) &gt;= 0
 * c2 = -(3 - 0.625 f1^2 - f2) (3 - 7 f1^2 - f2) &gt;= 0
 * c3 = (1.62 - 0.18 f1^2 - f2) (1.125 - 0.125 f1^2 - f2) &gt;= 0
 * c4 = -(2.07 - 0.23 f1^2 - f2) (0.63 - 0.07 f1^2 - f2) &gt;= 0
 * g3 = 1 + sum_{i=2..n} 2 (x_i + (x_{i-1} - 0.5)^2 - 1)^2
 * x in [0, sqrt(2)]^n
 * </pre>
 *
 * <p>The available pages of the paper define MW1-MW6 only; this definition is that of pymoo, which
 * states that it follows Ma and Wang's C++ code, and agrees with the type, decision space, number
 * of constraints and distance function that Table II of the paper gives. The constraints follow
 * jMetal's convention (a value &gt;= 0 is satisfied). PlatEMO takes x in [0, 1]^n and f1 =
 * sqrt(1.9999) g3 x1, while its g3 uses the unscaled x1: it has the same constraints and Pareto
 * front, but another Pareto set. The square root takes max(0, 2 - (f1 / g3)^2), which is negative
 * only by rounding (with x1 = sqrt(2), the upper bound, in floating point).
 */
@SuppressWarnings("serial")
public class MW11 extends AbstractDoubleProblem {

  /** Creates the problem with 15 variables. */
  public MW11() {
    this(15);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public MW11(int numberOfVariables) {
    MWFunctions.setUp(this, "MW11", numberOfVariables, 2, 4, Math.sqrt(2.0));
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = MWFunctions.variables(solution);
    double g = MWFunctions.g3(x, 2);
    double f1 = g * x[0];
    double f2 = g * MWFunctions.sqrtOfNonNegative(2.0 - (f1 / g) * (f1 / g));
    double s = f1 * f1;

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] = (3.0 - s - f2) * (3.0 - 2.0 * s - f2);
    solution.constraints()[1] = -(3.0 - 0.625 * s - f2) * (3.0 - 7.0 * s - f2);
    solution.constraints()[2] = (1.62 - 0.18 * s - f2) * (1.125 - 0.125 * s - f2);
    solution.constraints()[3] = -(2.07 - 0.23 * s - f2) * (0.63 - 0.07 * s - f2);
    return solution;
  }
}
