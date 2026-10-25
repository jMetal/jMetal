package org.uma.jmetal.problem.multiobjective.dcdtlz;

/**
 * Problem DC3-DTLZ3 of Li, Chen, Fu and Yao (IEEE TEVC, 2019; see {@link DCDTLZ}): the
 * objectives of DTLZ3, with m constraints
 *
 * <pre>
 * c_i = cos(a pi x_i) - b &gt;= 0,  i = 1..m-1
 * c_m = cos(a pi g) - b &gt;= 0
 * </pre>
 *
 * <p>With the default constants (a = 3, b = 0.5 and a factor of g of 100; see {@link DCDTLZ}),
 * every position variable must be in [0, 1/9] or [5/9, 7/9], and the front is made of the parts of
 * the unit sphere that they reach.
 */
@SuppressWarnings("serial")
public class DC3_DTLZ3 extends DCDTLZ {

  /** Creates the problem with 3 objectives and 12 variables. */
  public DC3_DTLZ3() {
    this(12, 3);
  }

  /**
   * Creates the problem with the default constants.
   *
   * @param numberOfVariables the number of variables, at least the number of objectives minus one
   * @param numberOfObjectives the number of objectives, at least two
   */
  public DC3_DTLZ3(int numberOfVariables, int numberOfObjectives) {
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
  public DC3_DTLZ3(
      int numberOfVariables, int numberOfObjectives, double a, double b, double gFactor) {
    super("DC3_DTLZ3", numberOfVariables, numberOfObjectives, numberOfObjectives, a, b, gFactor);
  }

  @Override
  protected void evaluateObjectives(double[] x, double g, double[] f) {
    sphericalObjectives(x, g, f);
  }

  @Override
  protected void evaluateConstraints(double[] x, double g, double[] c) {
    for (int i = 0; i < c.length - 1; i++) {
      c[i] = cosineConstraint(x[i]);
    }
    c[c.length - 1] = cosineConstraint(g);
  }
}
