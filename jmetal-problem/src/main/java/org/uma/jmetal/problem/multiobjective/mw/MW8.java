package org.uma.jmetal.problem.multiobjective.mw;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MW8 of Z. Ma and Y. Wang, "Evolutionary Constrained Multiobjective Optimization: Test
 * Suite Construction and Performance Comparisons", IEEE Transactions on Evolutionary Computation
 * 23(6):972-986, 2019 (https://doi.org/10.1109/TEVC.2019.2896967), scalable in the number of
 * objectives m. Its Pareto front is made of disconnected parts of the unconstrained one, the unit
 * sphere: the bands where |sin(6 l)| &lt;= 1 / sqrt(2) (Type II).
 *
 * <pre>
 * f_1 = g2 cos(pi/2 x_1) cos(pi/2 x_2) ... cos(pi/2 x_{m-1})
 * f_k = g2 sin(pi/2 x_{m-k+1}) cos(pi/2 x_1) ... cos(pi/2 x_{m-k}),  k = 2, ..., m - 1
 * f_m = g2 sin(pi/2 x_1)
 * c   = (1.25 - 0.5 sin(6 l)^2)^2 - (f_1^2 + ... + f_m^2) &gt;= 0,
 * l   = arcsin(f_m / sqrt(f_1^2 + ... + f_m^2))
 * g2  = 1 + sum_{i=m..n} (1.5 + 0.1 / n z_i^2 - 1.5 cos(2 pi z_i)),  z_i = 1 - exp(-10 (x_i - (i - 1) / n)^2)
 * x in [0, 1]^n
 * </pre>
 *
 * <p>The available pages of the paper define MW1-MW6 only; this definition is that of pymoo, which
 * states that it follows Ma and Wang's C++ code, and agrees with the type, decision space, number
 * of constraints and distance function that Table II of the paper gives. The constraint follows
 * jMetal's convention (a value &gt;= 0 is satisfied). PlatEMO defines the problem in the same way.
 * The argument of arcsin is limited to 1, which it exceeds only by rounding.
 */
@SuppressWarnings("serial")
public class MW8 extends AbstractDoubleProblem {

  /** Creates the problem with 15 variables and 3 objectives. */
  public MW8() {
    this(15, 3);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least the number of objectives
   * @param numberOfObjectives the number of objectives, at least 2
   */
  public MW8(int numberOfVariables, int numberOfObjectives) {
    MWFunctions.setUp(this, "MW8", numberOfVariables, numberOfObjectives, 1, 1.0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = MWFunctions.variables(solution);
    int m = numberOfObjectives();
    double g = MWFunctions.g2(x, m);

    // product[j] = cos(pi/2 x_1) ... cos(pi/2 x_j)
    double[] product = new double[m];
    product[0] = 1.0;
    for (int j = 1; j < m; j++) {
      product[j] = product[j - 1] * Math.cos(0.5 * Math.PI * x[j - 1]);
    }
    double[] f = solution.objectives();
    f[0] = g * product[m - 1];
    for (int k = 2; k <= m; k++) {
      f[k - 1] = g * Math.sin(0.5 * Math.PI * x[m - k]) * product[m - k];
    }

    double squaredNorm = 0.0;
    for (double value : f) {
      squaredNorm += value * value;
    }
    double l = Math.asin(Math.min(1.0, f[m - 1] / Math.sqrt(squaredNorm)));
    double sine = Math.sin(6.0 * l);
    double a = 1.25 - 0.5 * sine * sine;
    solution.constraints()[0] = a * a - squaredNorm;
    return solution;
  }
}
