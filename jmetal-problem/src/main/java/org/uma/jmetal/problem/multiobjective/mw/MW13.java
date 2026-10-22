package org.uma.jmetal.problem.multiobjective.mw;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MW13 of Z. Ma and Y. Wang, "Evolutionary Constrained Multiobjective Optimization: Test
 * Suite Construction and Performance Comparisons", IEEE Transactions on Evolutionary Computation
 * 23(6):972-986, 2019 (https://doi.org/10.1109/TEVC.2019.2896967). Its Pareto front is made of
 * parts of the unconstrained one and of the boundary of the feasible region, in three
 * disconnected pieces (Type III).
 *
 * <pre>
 * f1 = g2 x1
 * f2 = g2 (5 - exp(f1 / g2) - |0.5 sin(3 pi f1 / g2)|)
 * c1 = (5 - (1 + f1 + 0.5 f1^2) - 0.5 sin(3 pi f1) - f2) (5 - (1 + 0.7 f1) - 0.5 sin(3 pi f1) - f2) &gt;= 0
 * c2 = -(5 - exp(f1) - 0.5 sin(3 pi f1) - f2) (5 - (1 + 0.4 f1) - 0.5 sin(3 pi f1) - f2) &gt;= 0
 * g2 = 1 + sum_{i=2..n} (1.5 + 0.1 / n z_i^2 - 1.5 cos(2 pi z_i)),  z_i = 1 - exp(-10 (x_i - (i - 1) / n)^2)
 * x in [0, 1.5]^n
 * </pre>
 *
 * <p>The available pages of the paper define MW1-MW6 only; this definition is that of pymoo, which
 * states that it follows Ma and Wang's C++ code, and agrees with the type, decision space, number
 * of constraints and distance function that Table II of the paper gives. The constraints follow
 * jMetal's convention (a value &gt;= 0 is satisfied). PlatEMO takes x in [0, 1]^n and f1 = 1.5 g2
 * x1, which is the same problem but for the range of the distance variables, and has the same
 * constraints in the reverse order; pymoo's order is kept, which puts first, as in MW10 and MW11
 * (where both agree), the constraint with the leading minus sign in pymoo's and PlatEMO's
 * convention (c &lt;= 0).
 */
@SuppressWarnings("serial")
public class MW13 extends AbstractDoubleProblem {

  /** Creates the problem with 15 variables. */
  public MW13() {
    this(15);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public MW13(int numberOfVariables) {
    MWFunctions.setUp(this, "MW13", numberOfVariables, 2, 2, 1.5);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = MWFunctions.variables(solution);
    double g = MWFunctions.g2(x, 2);
    double f1 = g * x[0];
    double f2 = g * (5.0 - Math.exp(f1 / g) - Math.abs(0.5 * Math.sin(3.0 * Math.PI * f1 / g)));
    double s = 0.5 * Math.sin(3.0 * Math.PI * f1);

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] =
        (5.0 - (1.0 + f1 + 0.5 * f1 * f1) - s - f2) * (5.0 - (1.0 + 0.7 * f1) - s - f2);
    solution.constraints()[1] = -(5.0 - Math.exp(f1) - s - f2) * (5.0 - (1.0 + 0.4 * f1) - s - f2);
    return solution;
  }
}
