package org.uma.jmetal.problem.multiobjective.mw;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MW3 of Z. Ma and Y. Wang, "Evolutionary Constrained Multiobjective Optimization: Test
 * Suite Construction and Performance Comparisons", IEEE Transactions on Evolutionary Computation
 * 23(6):972-986, 2019 (https://doi.org/10.1109/TEVC.2019.2896967), Eq. 17. Its Pareto front is
 * made of parts of the unconstrained one and of the boundary of the feasible region (Type III).
 *
 * <pre>
 * f1 = x1
 * f2 = g3 (1 - f1 / g3)
 * c1 = 1.05 - f1 - f2 + 0.45 sin(0.75 pi l)^6 &gt;= 0
 * c2 = f1 + f2 - 0.85 - 0.3 sin(0.75 pi l)^2 &gt;= 0,  l = sqrt(2) f2 - sqrt(2) f1
 * g3 = 1 + sum_{i=2..n} 2 (x_i + (x_{i-1} - 0.5)^2 - 1)^2
 * x in [0, 1]^n
 * </pre>
 *
 * <p>The constraints follow jMetal's convention (a value &gt;= 0 is satisfied): c2 is the paper's
 * 0.85 - f1 - f2 + 0.3 sin(0.75 pi l)^2 &lt;= 0 with its sign reversed. PlatEMO and pymoo define
 * the problem in the same way.
 */
@SuppressWarnings("serial")
public class MW3 extends AbstractDoubleProblem {

  /** Creates the problem with 15 variables. */
  public MW3() {
    this(15);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public MW3(int numberOfVariables) {
    MWFunctions.setUp(this, "MW3", numberOfVariables, 2, 2, 1.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = MWFunctions.variables(solution);
    double g = MWFunctions.g3(x, 2);
    double f1 = x[0];
    double f2 = g * (1.0 - f1 / g);
    double l = Math.sqrt(2.0) * f2 - Math.sqrt(2.0) * f1;

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] =
        1.05 - f1 - f2 + 0.45 * Math.pow(Math.sin(0.75 * Math.PI * l), 6);
    solution.constraints()[1] =
        -(0.85 - f1 - f2 + 0.3 * Math.pow(Math.sin(0.75 * Math.PI * l), 2));
    return solution;
  }
}
