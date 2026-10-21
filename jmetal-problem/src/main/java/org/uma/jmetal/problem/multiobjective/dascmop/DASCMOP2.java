package org.uma.jmetal.problem.multiobjective.dascmop;

/**
 * Problem DAS-CMOP2 (Fan et al., Evolutionary Computation, 2020; see {@link AbstractDASCMOP}),
 * with 2 objectives:
 *
 * <pre>
 * f = (x1, 1 - sqrt(x1)) + g
 * g = sum_{j=1..n} (x_j - sin(pi x1/2))^2
 * </pre>
 *
 * <p>The default difficulty triplet (eta, zeta, gamma) is (0, 0.5, 0.5), that of PlatEMO's
 * DASCMOP2.
 */
@SuppressWarnings("serial")
public class DASCMOP2 extends AbstractDASCMOP {

  /** Creates the problem with 30 variables and the difficulty triplet (0, 0.5, 0.5). */
  public DASCMOP2() {
    this(30);
  }

  /**
   * Creates the problem with the difficulty triplet (0, 0.5, 0.5).
   *
   * @param numberOfVariables the number of variables
   */
  public DASCMOP2(int numberOfVariables) {
    this(numberOfVariables, 0.0, 0.5, 0.5);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables
   * @param eta the diversity-hardness, in [0, 1]
   * @param zeta the feasibility-hardness, in [0, 1]
   * @param gamma the convergence-hardness, in [0, 1]
   */
  public DASCMOP2(int numberOfVariables, double eta, double zeta, double gamma) {
    super("DASCMOP2", numberOfVariables, 2, eta, zeta, gamma);
  }

  @Override
  protected double distance(double[] x) {
    return sineDistance(x);
  }

  @Override
  protected double[] shape(double[] x) {
    return new double[] {x[0], 1.0 - Math.sqrt(x[0])};
  }
}
