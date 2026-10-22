package org.uma.jmetal.problem.multiobjective.mw;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MW14 of Z. Ma and Y. Wang, "Evolutionary Constrained Multiobjective Optimization: Test
 * Suite Construction and Performance Comparisons", IEEE Transactions on Evolutionary Computation
 * 23(6):972-986, 2019 (https://doi.org/10.1109/TEVC.2019.2896967), scalable in the number of
 * objectives m. The whole unconstrained Pareto front, disconnected, is feasible (Type I): the
 * points with every x_i, i &lt; m, in [0, 0.7313523] or in (1.3296339, 1.5].
 *
 * <pre>
 * f_i = x_i,  i = 1, ..., m - 1
 * f_m = g3 / (m - 1) sum_{i=1..m-1} (6 - exp(f_i) - 1.5 sin(1.1 pi f_i^2))
 * c   = 1 / (m - 1) sum_{i=1..m-1} (6.1 - 1 - f_i - 0.5 f_i^2 - 1.5 sin(1.1 pi f_i^2)) - f_m &gt;= 0
 * g3  = 1 + sum_{i=m..n} 2 (x_i + (x_{i-1} - 0.5)^2 - 1)^2
 * x in [0, 1.5]^n
 * </pre>
 *
 * <p>The available pages of the paper define MW1-MW6 only; this definition is that of pymoo, which
 * states that it follows Ma and Wang's C++ code, and agrees with the type, decision space, number
 * of constraints and distance function that Table II of the paper gives. The constraint follows
 * jMetal's convention (a value &gt;= 0 is satisfied). PlatEMO takes x in [0, 1]^n and multiplies
 * every variable by 1.5, which is the same problem.
 */
@SuppressWarnings("serial")
public class MW14 extends AbstractDoubleProblem {

  /** Creates the problem with 15 variables and 3 objectives. */
  public MW14() {
    this(15, 3);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least the number of objectives
   * @param numberOfObjectives the number of objectives, at least 2
   */
  public MW14(int numberOfVariables, int numberOfObjectives) {
    MWFunctions.setUp(this, "MW14", numberOfVariables, numberOfObjectives, 1, 1.5);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = MWFunctions.variables(solution);
    int m = numberOfObjectives();
    double g = MWFunctions.g3(x, m);
    double[] f = solution.objectives();

    double shape = 0.0;
    double bound = 0.0;
    for (int i = 0; i < m - 1; i++) {
      f[i] = x[i];
      double wave = 1.5 * Math.sin(1.1 * Math.PI * f[i] * f[i]);
      shape += 6.0 - Math.exp(f[i]) - wave;
      bound += 6.1 - 1.0 - f[i] - 0.5 * f[i] * f[i] - wave;
    }
    f[m - 1] = g / (m - 1) * shape;
    solution.constraints()[0] = bound / (m - 1) - f[m - 1];
    return solution;
  }
}
