package org.uma.jmetal.problem.multiobjective.dascmop;

/**
 * Problem DAS-CMOP8 (Fan et al., Evolutionary Computation, 2020; see {@link AbstractDASCMOP}),
 * with 3 objectives:
 *
 * <pre>
 * f = (cos(pi x1/2) cos(pi x2/2), cos(pi x1/2) sin(pi x2/2), sin(pi x1/2)) + g
 * g = (n - 2) + sum_{j=3..n} ((x_j - 0.5)^2 - cos(20 pi (x_j - 0.5)))
 * </pre>
 *
 * <p>The default difficulty triplet (eta, zeta, gamma) is (0.5, 0.5, 0.5), that of PlatEMO's
 * DASCMOP8.
 */
@SuppressWarnings("serial")
public class DASCMOP8 extends AbstractDASCMOP {

  /** Creates the problem with 30 variables and the difficulty triplet (0.5, 0.5, 0.5). */
  public DASCMOP8() {
    this(30);
  }

  /**
   * Creates the problem with the difficulty triplet (0.5, 0.5, 0.5).
   *
   * @param numberOfVariables the number of variables
   */
  public DASCMOP8(int numberOfVariables) {
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
  public DASCMOP8(int numberOfVariables, double eta, double zeta, double gamma) {
    super("DASCMOP8", numberOfVariables, 3, eta, zeta, gamma);
  }

  @Override
  protected double distance(double[] x) {
    return cosineDistance(x, 2);
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
