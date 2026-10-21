package org.uma.jmetal.problem.multiobjective.mw;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MW9 of Z. Ma and Y. Wang, "Evolutionary Constrained Multiobjective Optimization: Test
 * Suite Construction and Performance Comparisons", IEEE Transactions on Evolutionary Computation
 * 23(6):972-986, 2019 (https://doi.org/10.1109/TEVC.2019.2896967). The unconstrained Pareto front
 * is infeasible but for its ends, and the Pareto front lies on the boundary of the feasible region
 * (Type IV).
 *
 * <pre>
 * f1 = g1 x1
 * f2 = g1 (1 - (f1 / g1)^0.6)
 * c  = -min(T1, T2 T3) &gt;= 0
 * T1 = (1 - 0.64 f1^2 - f2) (1 - 0.36 f1^2 - f2)
 * T2 = 1.35^2 - (f1 + 0.35)^2 - f2
 * T3 = 1.15^2 - (f1 + 0.15)^2 - f2
 * g1 = 1 + sum_{i=2..n} (1 - exp(-10 (x_i^(n-2) - 0.5 - (i - 1) / (2 n))^2))
 * x in [0, 1]^n
 * </pre>
 *
 * <p>The available pages of the paper define MW1-MW6 only; this definition is that of pymoo, which
 * states that it follows Ma and Wang's C++ code, and agrees with the type, decision space, number
 * of constraints and distance function that Table II of the paper gives. The constraint follows
 * jMetal's convention (a value &gt;= 0 is satisfied). PlatEMO defines the problem in the same way.
 */
@SuppressWarnings("serial")
public class MW9 extends AbstractDoubleProblem {

  /** Creates the problem with 15 variables. */
  public MW9() {
    this(15);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public MW9(int numberOfVariables) {
    MWFunctions.setUp(this, "MW9", numberOfVariables, 2, 1, 1.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = MWFunctions.variables(solution);
    double g = MWFunctions.g1(x, 2);
    double f1 = g * x[0];
    double f2 = g * (1.0 - Math.pow(f1 / g, 0.6));
    double t1 = (1.0 - 0.64 * f1 * f1 - f2) * (1.0 - 0.36 * f1 * f1 - f2);
    double t2 = 1.35 * 1.35 - (f1 + 0.35) * (f1 + 0.35) - f2;
    double t3 = 1.15 * 1.15 - (f1 + 0.15) * (f1 + 0.15) - f2;

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] = -Math.min(t1, t2 * t3);
    return solution;
  }
}
