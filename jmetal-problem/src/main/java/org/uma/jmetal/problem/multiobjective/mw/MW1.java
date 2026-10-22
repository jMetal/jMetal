package org.uma.jmetal.problem.multiobjective.mw;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MW1 of Z. Ma and Y. Wang, "Evolutionary Constrained Multiobjective Optimization: Test
 * Suite Construction and Performance Comparisons", IEEE Transactions on Evolutionary Computation
 * 23(6):972-986, 2019 (https://doi.org/10.1109/TEVC.2019.2896967), Eq. 15. Its Pareto front is made
 * of disconnected parts of the unconstrained one (Type II).
 *
 * <pre>
 * f1 = x1
 * f2 = g1 (1 - 0.85 f1 / g1)
 * c  = 1 - f1 - f2 + 0.5 sin(2 pi l)^8 &gt;= 0,  l = sqrt(2) f2 - sqrt(2) f1
 * g1 = 1 + sum_{i=2..n} (1 - exp(-10 (x_i^(n-2) - 0.5 - (i - 1) / (2 n))^2))
 * x in [0, 1]^n
 * </pre>
 *
 * <p>The constraint follows jMetal's convention (a value &gt;= 0 is satisfied). PlatEMO and pymoo
 * define the problem in the same way.
 */
@SuppressWarnings("serial")
public class MW1 extends AbstractDoubleProblem {

  /** Creates the problem with 15 variables. */
  public MW1() {
    this(15);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public MW1(int numberOfVariables) {
    MWFunctions.setUp(this, "MW1", numberOfVariables, 2, 1, 1.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = MWFunctions.variables(solution);
    double g = MWFunctions.g1(x, 2);
    double f1 = x[0];
    double f2 = g * (1.0 - 0.85 * f1 / g);
    double l = Math.sqrt(2.0) * f2 - Math.sqrt(2.0) * f1;

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] = 1.0 - f1 - f2 + 0.5 * Math.pow(Math.sin(2.0 * Math.PI * l), 8);
    return solution;
  }
}
