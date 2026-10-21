package org.uma.jmetal.problem.multiobjective.dascmop;

/**
 * Problem DAS-CMOP9 (Fan et al., Evolutionary Computation, 2020; see {@link AbstractDASCMOP}),
 * with 3 objectives:
 *
 * <pre>
 * f = (cos(pi x1/2) cos(pi x2/2), cos(pi x1/2) sin(pi x2/2), sin(pi x1/2)) + g
 * g = sum_{j=3..n} (x_j - cos(0.25 (j/n) pi (x1 + x2)))^2
 * </pre>
 *
 * <p>The default difficulty triplet (eta, zeta, gamma) is (0.5, 0.5, 0.5), that of PlatEMO's
 * DASCMOP9.
 */
@SuppressWarnings("serial")
public class DASCMOP9 extends AbstractDASCMOP {

  /** Creates the problem with 30 variables and the difficulty triplet (0.5, 0.5, 0.5). */
  public DASCMOP9() {
    this(30);
  }

  /**
   * Creates the problem with the difficulty triplet (0.5, 0.5, 0.5).
   *
   * @param numberOfVariables the number of variables
   */
  public DASCMOP9(int numberOfVariables) {
    this(numberOfVariables, 0.5, 0.5, 0.5);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables
   * @param eta the diversity-hardness, in [0, 1]
   * @param zeta the feasibility-hardness, in [0, 1]
   * @param gamma the convergence-hardness, in [0, 1]
   */
  public DASCMOP9(int numberOfVariables, double eta, double zeta, double gamma) {
    super("DASCMOP9", numberOfVariables, 3, eta, zeta, gamma);
  }

  /**
   * The distance function of the paper, in which the target of each variable x_j depends on j.
   * PlatEMO's DASCMOP9 uses n - 2 instead of j for every variable; both have the same Pareto front.
   */
  @Override
  protected double distance(double[] x) {
    int n = x.length;
    double result = 0.0;
    for (int j = 3; j <= n; j++) {
      double target = Math.cos(0.25 * j / n * Math.PI * (x[0] + x[1]));
      result += (x[j - 1] - target) * (x[j - 1] - target);
    }
    return result;
  }

  @Override
  protected double[] shape(double[] x) {
    return new double[] {
      Math.cos(0.5 * Math.PI * x[0]) * Math.cos(0.5 * Math.PI * x[1]),
      Math.cos(0.5 * Math.PI * x[0]) * Math.sin(0.5 * Math.PI * x[1]),
      Math.sin(0.5 * Math.PI * x[0])
    };
  }
}
