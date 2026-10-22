package org.uma.jmetal.problem.multiobjective.mw;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MW2 of Z. Ma and Y. Wang, "Evolutionary Constrained Multiobjective Optimization: Test
 * Suite Construction and Performance Comparisons", IEEE Transactions on Evolutionary Computation
 * 23(6):972-986, 2019 (https://doi.org/10.1109/TEVC.2019.2896967), Eq. 16. The whole unconstrained
 * Pareto front, the segment f1 + f2 = 1, is feasible (Type I).
 *
 * <pre>
 * f1 = x1
 * f2 = g2 (1 - f1 / g2)
 * c  = 1 - f1 - f2 + 0.5 sin(3 pi l)^8 &gt;= 0,  l = sqrt(2) f2 - sqrt(2) f1
 * g2 = 1 + sum_{i=2..n} (1.5 + 0.1 / n z_i^2 - 1.5 cos(2 pi z_i)),  z_i = 1 - exp(-10 (x_i - (i - 1) / n)^2)
 * x in [0, 1]^n
 * </pre>
 *
 * <p>The constraint follows jMetal's convention (a value &gt;= 0 is satisfied). PlatEMO and pymoo
 * define the problem in the same way.
 */
@SuppressWarnings("serial")
public class MW2 extends AbstractDoubleProblem {

  /** Creates the problem with 15 variables. */
  public MW2() {
    this(15);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public MW2(int numberOfVariables) {
    MWFunctions.setUp(this, "MW2", numberOfVariables, 2, 1, 1.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = MWFunctions.variables(solution);
    double g = MWFunctions.g2(x, 2);
    double f1 = x[0];
    double f2 = g * (1.0 - f1 / g);
    double l = Math.sqrt(2.0) * f2 - Math.sqrt(2.0) * f1;

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.constraints()[0] = 1.0 - f1 - f2 + 0.5 * Math.pow(Math.sin(3.0 * Math.PI * l), 8);
    return solution;
  }
}
