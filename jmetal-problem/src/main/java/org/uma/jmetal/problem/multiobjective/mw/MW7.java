package org.uma.jmetal.problem.multiobjective.mw;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MW7 of Z. Ma and Y. Wang, "Evolutionary Constrained Multiobjective Optimization: Test
 * Suite Construction and Performance Comparisons", IEEE Transactions on Evolutionary Computation
 * 23(6):972-986, 2019 (https://doi.org/10.1109/TEVC.2019.2896967). Its Pareto front is made of
 * parts of the unconstrained one, the quarter of the unit circle, and of the boundary of the
 * feasible region (Type III).
 *
 * <pre>
 * f1 = g3 x1
 * f2 = g3 sqrt(1 - (f1 / g3)^2)
 * c1 = (1.2 + 0.4 sin(4 l)^16)^2 - f1^2 - f2^2 &gt;= 0
 * c2 = f1^2 + f2^2 - (1.15 - 0.2 sin(4 l)^8)^2 &gt;= 0,  l = arctan(f2 / f1)
 * g3 = 1 + sum_{i=2..n} 2 (x_i + (x_{i-1} - 0.5)^2 - 1)^2
 * x in [0, 1]^n
 * </pre>
 *
 * <p>The available pages of the paper define MW1-MW6 only; this definition is that of pymoo, which
 * states that it follows Ma and Wang's C++ code, and agrees with the type, decision space, number
 * of constraints and distance function that Table II of the paper gives. The constraints follow
 * jMetal's convention (a value &gt;= 0 is satisfied). PlatEMO defines the problem in the same way.
 * The square root takes max(0, 1 - (f1 / g3)^2), which is negative only by rounding.
 */
@SuppressWarnings("serial")
public class MW7 extends AbstractDoubleProblem {

  /** Creates the problem with 15 variables. */
  public MW7() {
    this(15);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public MW7(int numberOfVariables) {
    MWFunctions.setUp(this, "MW7", numberOfVariables, 2, 2, 1.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = MWFunctions.variables(solution);
    double g = MWFunctions.g3(x, 2);
    double f1 = g * x[0];
    double f2 = g * MWFunctions.sqrtOfNonNegative(1.0 - (f1 / g) * (f1 / g));
    double l = Math.atan(f2 / f1);
    double squaredNorm = f1 * f1 + f2 * f2;
    double a = 1.2 + 0.4 * Math.pow(Math.sin(4.0 * l), 16);
    double b = 1.15 - 0.2 * Math.pow(Math.sin(4.0 * l), 8);

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] = a * a - squaredNorm;
    solution.constraints()[1] = squaredNorm - b * b;
    return solution;
  }
}
