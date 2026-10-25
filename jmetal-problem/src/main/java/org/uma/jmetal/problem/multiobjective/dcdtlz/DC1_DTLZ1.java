package org.uma.jmetal.problem.multiobjective.dcdtlz;

/**
 * Problem DC1-DTLZ1 of Li, Chen, Fu and Yao (IEEE TEVC, 2019; see {@link DCDTLZ}): the
 * objectives of DTLZ1, with one constraint
 *
 * <pre>
 * c = cos(a pi x_1) - b &gt;= 0
 * </pre>
 *
 * <p>With the default constants (a = 3, b = 0.5 and a factor of g of 100, as PlatEMO), x_1 must be
 * in [0, 1/9] or [5/9, 7/9], so the front is made of the parts of the simplex sum f = 0.5 with f_m
 * in [4/9, 1/2] or [1/9, 2/9].
 */
@SuppressWarnings("serial")
public class DC1_DTLZ1 extends DCDTLZ {

  /** Creates the problem with 3 objectives and 7 variables. */
  public DC1_DTLZ1() {
    this(7, 3);
  }

  /**
   * Creates the problem with the default constants.
   *
   * @param numberOfVariables the number of variables, at least the number of objectives
   * @param numberOfObjectives the number of objectives, at least two
   */
  public DC1_DTLZ1(int numberOfVariables, int numberOfObjectives) {
    this(numberOfVariables, numberOfObjectives, 3.0, 0.5, 100.0);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least the number of objectives
   * @param numberOfObjectives the number of objectives, at least two
   * @param a the frequency of the constraints, positive
   * @param b their threshold, in [-1, 1]
   * @param gFactor the factor of g, positive
   */
  public DC1_DTLZ1(
      int numberOfVariables, int numberOfObjectives, double a, double b, double gFactor) {
    super("DC1_DTLZ1", numberOfVariables, numberOfObjectives, 1, a, b, gFactor);
  }

  @Override
  protected void evaluateObjectives(double[] x, double g, double[] f) {
    linearObjectives(x, g, f);
  }

  @Override
  protected void evaluateConstraints(double[] x, double g, double[] c) {
    c[0] = cosineConstraint(x[0]);
  }
}
