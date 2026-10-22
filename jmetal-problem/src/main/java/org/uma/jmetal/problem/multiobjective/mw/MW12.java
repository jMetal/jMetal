package org.uma.jmetal.problem.multiobjective.mw;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MW12 of Z. Ma and Y. Wang, "Evolutionary Constrained Multiobjective Optimization: Test
 * Suite Construction and Performance Comparisons", IEEE Transactions on Evolutionary Computation
 * 23(6):972-986, 2019 (https://doi.org/10.1109/TEVC.2019.2896967). The unconstrained Pareto front
 * is infeasible, and the Pareto front lies on the boundary of the feasible region (Type IV).
 *
 * <pre>
 * f1 = g1 x1
 * f2 = g1 (0.85 - 0.8 (f1 / g1) - 0.08 |sin(3.2 pi (f1 / g1))|)
 * c1 = (1 - 0.625 f1 - f2 + 0.08 sin(2 pi (f2 - f1 / 1.6)))
 *      (1.4 - 0.875 f1 - f2 + 0.08 sin(2 pi (f2 / 1.4 - f1 / 1.6))) &gt;= 0
 * c2 = -(1 - 0.8 f1 - f2 + 0.08 sin(2 pi (f2 - f1 / 1.5)))
 *       (1.8 - 1.125 f1 - f2 + 0.08 sin(2 pi (f2 / 1.8 - f1 / 1.6))) &gt;= 0
 * g1 = 1 + sum_{i=2..n} (1 - exp(-10 (x_i^(n-2) - 0.5 - (i - 1) / (2 n))^2))
 * x in [0, 1]^n
 * </pre>
 *
 * <p>The available pages of the paper define MW1-MW6 only; this definition is that of pymoo, which
 * states that it follows Ma and Wang's C++ code, and agrees with the type, decision space, number
 * of constraints and distance function that Table II of the paper gives. The constraints follow
 * jMetal's convention (a value &gt;= 0 is satisfied). PlatEMO has the same constraints in the
 * reverse order; pymoo's order is kept, which puts first, as in MW10 and MW11 (where both agree),
 * the constraint with the leading minus sign in pymoo's and PlatEMO's convention (c &lt;= 0).
 */
@SuppressWarnings("serial")
public class MW12 extends AbstractDoubleProblem {

  /** Creates the problem with 15 variables. */
  public MW12() {
    this(15);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public MW12(int numberOfVariables) {
    MWFunctions.setUp(this, "MW12", numberOfVariables, 2, 2, 1.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = MWFunctions.variables(solution);
    double g = MWFunctions.g1(x, 2);
    double f1 = g * x[0];
    double f2 = g * (0.85 - 0.8 * (f1 / g) - 0.08 * Math.abs(Math.sin(3.2 * Math.PI * (f1 / g))));
    double twoPi = 2.0 * Math.PI;

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] =
        (1.0 - 0.625 * f1 - f2 + 0.08 * Math.sin(twoPi * (f2 - f1 / 1.6)))
            * (1.4 - 0.875 * f1 - f2 + 0.08 * Math.sin(twoPi * (f2 / 1.4 - f1 / 1.6)));
    solution.constraints()[1] =
        -(1.0 - 0.8 * f1 - f2 + 0.08 * Math.sin(twoPi * (f2 - f1 / 1.5)))
            * (1.8 - 1.125 * f1 - f2 + 0.08 * Math.sin(twoPi * (f2 / 1.8 - f1 / 1.6)));
    return solution;
  }
}
