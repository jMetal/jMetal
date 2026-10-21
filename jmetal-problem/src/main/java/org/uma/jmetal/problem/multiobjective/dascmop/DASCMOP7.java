package org.uma.jmetal.problem.multiobjective.dascmop;

/**
 * Problem DAS-CMOP7 (Fan et al., Evolutionary Computation, 2020; see {@link AbstractDASCMOP}),
 * with 3 objectives:
 *
 * <pre>
 * f = (x1 x2, x2 (1 - x1), 1 - x2) + g
 * g = (n - 2) + sum_{j=3..n} ((x_j - 0.5)^2 - cos(20 pi (x_j - 0.5)))
 * </pre>
 *
 * <p>The default difficulty triplet (eta, zeta, gamma) is (0.5, 0.5, 0.5), that of PlatEMO's
 * DASCMOP7.
 */
@SuppressWarnings("serial")
public class DASCMOP7 extends AbstractDASCMOP {

  /** Creates the problem with 30 variables and the difficulty triplet (0.5, 0.5, 0.5). */
  public DASCMOP7() {
    this(30);
  }

  /**
   * Creates the problem with the difficulty triplet (0.5, 0.5, 0.5).
   *
   * @param numberOfVariables the number of variables
   */
  public DASCMOP7(int numberOfVariables) {
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
  public DASCMOP7(int numberOfVariables, double eta, double zeta, double gamma) {
    super("DASCMOP7", numberOfVariables, 3, eta, zeta, gamma);
  }

  @Override
  protected double distance(double[] x) {
    return cosineDistance(x, 2);
  }

  @Override
  protected double[] shape(double[] x) {
    return new double[] {x[0] * x[1], x[1] * (1.0 - x[0]), 1.0 - x[1]};
  }
}
