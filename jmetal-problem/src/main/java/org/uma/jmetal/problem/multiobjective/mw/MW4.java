package org.uma.jmetal.problem.multiobjective.mw;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MW4 of Z. Ma and Y. Wang, "Evolutionary Constrained Multiobjective Optimization: Test
 * Suite Construction and Performance Comparisons", IEEE Transactions on Evolutionary Computation
 * 23(6):972-986, 2019 (https://doi.org/10.1109/TEVC.2019.2896967), Eq. 18, scalable in the number
 * of objectives m. The whole unconstrained Pareto front, the simplex f_1 + ... + f_m = 1, is
 * feasible (Type I).
 *
 * <pre>
 * f_1 = g1 (1 - x_1) (1 - x_2) ... (1 - x_{m-1})
 * f_k = g1 x_{m-k+1} (1 - x_1) ... (1 - x_{m-k}),  k = 2, ..., m - 1
 * f_m = g1 x_1
 * c   = 1 + 0.4 sin(2.5 pi l)^8 - (f_1 + ... + f_m) &gt;= 0,  l = f_m - (f_1 + ... + f_{m-1})
 * g1  = 1 + sum_{i=m..n} (1 - exp(-10 (x_i^(n-m) - 0.5 - (i - 1) / (2 n))^2))
 * x in [0, 1]^n
 * </pre>
 *
 * <p>The constraint follows jMetal's convention (a value &gt;= 0 is satisfied). pymoo follows the
 * paper. PlatEMO mirrors the position variables (f_1 = g1 x_1 ... x_{m-1}, ..., f_m = g1 (1 - x_1),
 * that is, x_i replaced by 1 - x_i for i &lt; m), which leaves the Pareto front unchanged.
 */
@SuppressWarnings("serial")
public class MW4 extends AbstractDoubleProblem {

  /** Creates the problem with 15 variables and 3 objectives. */
  public MW4() {
    this(15, 3);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least the number of objectives
   * @param numberOfObjectives the number of objectives, at least 2
   */
  public MW4(int numberOfVariables, int numberOfObjectives) {
    MWFunctions.setUp(this, "MW4", numberOfVariables, numberOfObjectives, 1, 1.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = MWFunctions.variables(solution);
    int m = numberOfObjectives();
    double g = MWFunctions.g1(x, m);

    // product[j] = (1 - x_1) ... (1 - x_j)
    double[] product = new double[m];
    product[0] = 1.0;
    for (int j = 1; j < m; j++) {
      product[j] = product[j - 1] * (1.0 - x[j - 1]);
    }
    double[] f = solution.objectives();
    f[0] = g * product[m - 1];
    for (int k = 2; k <= m; k++) {
      f[k - 1] = g * x[m - k] * product[m - k];
    }

    double sum = 0.0;
    for (int k = 0; k < m - 1; k++) {
      sum += f[k];
    }
    double l = f[m - 1] - sum;
    solution.constraints()[0] =
        1.0 + 0.4 * Math.pow(Math.sin(2.5 * Math.PI * l), 8) - (sum + f[m - 1]);
    return solution;
  }
}
