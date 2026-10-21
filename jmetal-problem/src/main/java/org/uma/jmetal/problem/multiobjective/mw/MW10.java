package org.uma.jmetal.problem.multiobjective.mw;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MW10 of Z. Ma and Y. Wang, "Evolutionary Constrained Multiobjective Optimization: Test
 * Suite Construction and Performance Comparisons", IEEE Transactions on Evolutionary Computation
 * 23(6):972-986, 2019 (https://doi.org/10.1109/TEVC.2019.2896967). Its Pareto front is made of
 * parts of the unconstrained one and of the boundary of the feasible region, in two disconnected
 * pieces (Type III).
 *
 * <pre>
 * f1 = g2 x1^n
 * f2 = g2 (1 - (f1 / g2)^2)
 * c1 = (2 - 4 f1^2 - f2) (2 - 8 f1^2 - f2) &gt;= 0
 * c2 = -(2 - 2 f1^2 - f2) (2 - 16 f1^2 - f2) &gt;= 0
 * c3 = -(1 - f1^2 - f2) (1.2 - 1.2 f1^2 - f2) &gt;= 0
 * g2 = 1 + sum_{i=2..n} (1.5 + 0.1 / n z_i^2 - 1.5 cos(2 pi z_i)),  z_i = 1 - exp(-10 (x_i - (i - 1) / n)^2)
 * x in [0, 1]^n
 * </pre>
 *
 * <p>The available pages of the paper define MW1-MW6 only; this definition is that of pymoo, which
 * states that it follows Ma and Wang's C++ code, and agrees with the type, decision space, number
 * of constraints and distance function that Table II of the paper gives. The constraints follow
 * jMetal's convention (a value &gt;= 0 is satisfied). PlatEMO defines the problem in the same way.
 */
@SuppressWarnings("serial")
public class MW10 extends AbstractDoubleProblem {

  /** Creates the problem with 15 variables. */
  public MW10() {
    this(15);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public MW10(int numberOfVariables) {
    MWFunctions.setUp(this, "MW10", numberOfVariables, 2, 3, 1.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = MWFunctions.variables(solution);
    double g = MWFunctions.g2(x, 2);
    double f1 = g * Math.pow(x[0], x.length);
    double f2 = g * (1.0 - (f1 / g) * (f1 / g));
    double s = f1 * f1;

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] = (2.0 - 4.0 * s - f2) * (2.0 - 8.0 * s - f2);
    solution.constraints()[1] = -(2.0 - 2.0 * s - f2) * (2.0 - 16.0 * s - f2);
    solution.constraints()[2] = -(1.0 - s - f2) * (1.2 - 1.2 * s - f2);
    return solution;
  }
}
