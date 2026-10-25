package org.uma.jmetal.problem.multiobjective.dcdtlz;

/**
 * Problem DC2-DTLZ1 of Li, Chen, Fu and Yao (IEEE TEVC, 2019; see {@link DCDTLZ}): the
 * objectives of DTLZ1, with two constraints
 *
 * <pre>
 * c_1 = cos(a pi g) - b &gt;= 0
 * c_2 = exp(-g) - b &gt;= 0
 * </pre>
 *
 * <p>With the default constants (a = 3, b = 0.5 and a factor of g of 100, as PlatEMO), g must be in
 * [0, 1/9] or [5/9, ln 2], and the front is the simplex sum f = 0.5.
 */
@SuppressWarnings("serial")
public class DC2_DTLZ1 extends DCDTLZ {

  /** Creates the problem with 3 objectives and 7 variables. */
  public DC2_DTLZ1() {
    this(7, 3);
  }

  /**
   * Creates the problem with the default constants.
   *
   * @param numberOfVariables the number of variables, at least the number of objectives minus one
   * @param numberOfObjectives the number of objectives, at least two
   */
  public DC2_DTLZ1(int numberOfVariables, int numberOfObjectives) {
    this(numberOfVariables, numberOfObjectives, 3.0, 0.5, 100.0);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least the number of objectives minus one
   * @param numberOfObjectives the number of objectives, at least two
   * @param a the frequency of the constraints, positive
   * @param b their threshold, in [-1, 1]
   * @param gFactor the factor of g, positive
   */
  public DC2_DTLZ1(
      int numberOfVariables, int numberOfObjectives, double a, double b, double gFactor) {
    super("DC2_DTLZ1", numberOfVariables, numberOfObjectives, 2, a, b, gFactor);
  }

  @Override
  protected void evaluateObjectives(double[] x, double g, double[] f) {
    linearObjectives(x, g, f);
  }

  @Override
  protected void evaluateConstraints(double[] x, double g, double[] c) {
    c[0] = cosineConstraint(g);
    c[1] = Math.exp(-g) - b();
  }
}
