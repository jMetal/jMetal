package org.uma.jmetal.problem.multiobjective.dcdtlz;

import java.util.Collections;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Base class of the DC-DTLZ problems of K. Li, R. Chen, G. Fu and X. Yao, "Two-Archive
 * Evolutionary Algorithm for Constrained Multiobjective Optimization", IEEE Transactions on
 * Evolutionary Computation 23(2):303-315, 2019 (https://doi.org/10.1109/TEVC.2018.2855411). They
 * add constraints to the objectives of DTLZ1 and DTLZ3:
 *
 * <ul>
 *   <li>DC1 constrains the first position variable, which cuts the front into disconnected parts;
 *   <li>DC2 constrains the distance function g, which leaves the front whole but surrounds it with
 *       infeasible regions;
 *   <li>DC3 constrains every position variable and g, which combines the two.
 * </ul>
 *
 * <p>With m objectives and n variables, x_1, ..., x_{m-1} are the position variables and x_m, ...,
 * x_n the k = n - m + 1 distance variables:
 *
 * <pre>
 * g = gFactor (k + sum_{i=m..n} ((x_i - 0.5)^2 - cos(20 pi (x_i - 0.5))))
 * DTLZ1: f_1 = 0.5 (1 + g) x_1 ... x_{m-1},  f_i = 0.5 (1 + g) x_1 ... x_{m-i} (1 - x_{m-i+1})
 * DTLZ3: f_1 = (1 + g) cos(x_1 pi / 2) ... cos(x_{m-1} pi / 2),
 *        f_i = (1 + g) cos(x_1 pi / 2) ... cos(x_{m-i} pi / 2) sin(x_{m-i+1} pi / 2)
 * x in [0, 1]^n
 * </pre>
 *
 * <p>The constraints are of the form cos(a pi y) - b &gt;= 0 (jMetal's convention: a value &gt;= 0
 * is satisfied), and DC2 adds exp(-g) - b &gt;= 0. The definitions are in the supplementary
 * material of the paper, and the implementations differ in a, b and the factor of g:
 *
 * <table>
 *   <caption>The constants of the implementations</caption>
 *   <tr>
 *     <th></th><th>EMOC (authors' group)</th><th>PlatEMO 4</th><th>cmo</th><th>pymoo</th>
 *   </tr>
 *   <tr><td>a, b of DC1</td><td>3, 0.5</td><td>3, 0.5</td><td>3, 0.5</td><td>5, 0.95</td></tr>
 *   <tr>
 *     <td>a, b of DC2-DTLZ1</td><td>3, 0.9</td><td>3, 0.5</td><td>3, 0.5</td>
 *     <td>3, 0.9 on g / 100</td>
 *   </tr>
 *   <tr>
 *     <td>a, b of DC2-DTLZ3</td><td>3, 0.5</td><td>3, 0.5</td><td>3, 0.5</td>
 *     <td>3, 0.9 on g / 100</td>
 *   </tr>
 *   <tr><td>a, b of DC3</td><td>3, 0.5</td><td>3, 0.5</td><td>3, 0.5</td><td>5, 0.5</td></tr>
 *   <tr><td>factor of g in DTLZ3</td><td>10</td><td>10</td><td>100</td><td>100</td></tr>
 * </table>
 *
 * <p>EMOC is the framework of the first author's group (COLA-Laboratory); cmo is the Python
 * package of the STEP benchmark (Cork, Tu&#353;ar and Filipi&#269;, Knowledge-Based Systems, 2026),
 * whose DC3 also constrains x_m. The default constructors follow PlatEMO, and the five-argument
 * ones give any of the other variants. Every implementation uses 100 as the factor of g in DTLZ1.
 *
 * <p>The reference fronts in {@code resources/referenceFronts} are those of the default constants.
 * Distance variables at 0.5 give g = 0, which satisfies every constraint on g, so the front of DC2
 * is that of DTLZ1 or DTLZ3, and those of DC1 and DC3 are the parts of it whose position variables
 * are feasible.
 */
@SuppressWarnings("serial")
public abstract class DCDTLZ extends AbstractDoubleProblem {

  private final double a;
  private final double b;
  private final double gFactor;

  /**
   * Checks the parameters and sets the name, dimensions and bounds: every variable in [0, 1].
   *
   * @param name the name of the problem
   * @param numberOfVariables the number of variables, at least the number of objectives
   * @param numberOfObjectives the number of objectives, at least two
   * @param numberOfConstraints the number of constraints
   * @param a the frequency of the constraints, positive
   * @param b their threshold, in [-1, 1]
   * @param gFactor the factor of g, positive
   */
  protected DCDTLZ(
      String name,
      int numberOfVariables,
      int numberOfObjectives,
      int numberOfConstraints,
      double a,
      double b,
      double gFactor) {
    Check.that(
        numberOfObjectives >= 2,
        () -> name + " needs at least two objectives (" + numberOfObjectives + ")");
    Check.that(
        numberOfVariables >= numberOfObjectives,
        () ->
            name
                + " needs at least as many variables ("
                + numberOfVariables
                + ") as objectives ("
                + numberOfObjectives
                + ")");
    Check.that(a > 0, () -> name + ": a must be positive (" + a + ")");
    Check.that(b >= -1 && b <= 1, () -> name + ": b must be in [-1, 1] (" + b + ")");
    Check.that(gFactor > 0, () -> name + ": the factor of g must be positive (" + gFactor + ")");
    this.a = a;
    this.b = b;
    this.gFactor = gFactor;
    name(name);
    numberOfObjectives(numberOfObjectives);
    numberOfConstraints(numberOfConstraints);
    variableBounds(
        Collections.nCopies(numberOfVariables, 0.0), Collections.nCopies(numberOfVariables, 1.0));
  }

  /** The frequency of the constraints. */
  public double a() {
    return a;
  }

  /** The threshold of the constraints. */
  public double b() {
    return b;
  }

  /** The factor of the distance function g. */
  public double gFactor() {
    return gFactor;
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = new double[numberOfVariables()];
    for (int i = 0; i < x.length; i++) {
      x[i] = solution.variables().get(i);
    }
    double g = g(x);
    evaluateObjectives(x, g, solution.objectives());
    evaluateConstraints(x, g, solution.constraints());
    return solution;
  }

  /**
   * Evaluates the objectives.
   *
   * @param x the variables
   * @param g the distance function
   * @param f the objectives to fill
   */
  protected abstract void evaluateObjectives(double[] x, double g, double[] f);

  /**
   * Evaluates the constraints.
   *
   * @param x the variables
   * @param g the distance function
   * @param c the constraints to fill
   */
  protected abstract void evaluateConstraints(double[] x, double g, double[] c);

  /** The distance function g of DTLZ1 and DTLZ3, with this problem's factor. */
  private double g(double[] x) {
    int k = x.length - numberOfObjectives() + 1;
    double sum = 0.0;
    for (int i = x.length - k; i < x.length; i++) {
      sum += (x[i] - 0.5) * (x[i] - 0.5) - Math.cos(20.0 * Math.PI * (x[i] - 0.5));
    }
    return gFactor * (k + sum);
  }

  /** The objectives of DTLZ1, whose front is the simplex sum f = 0.5. */
  protected static void linearObjectives(double[] x, double g, double[] f) {
    int m = f.length;
    for (int i = 0; i < m; i++) {
      f[i] = 0.5 * (1.0 + g);
      for (int j = 0; j < m - (i + 1); j++) {
        f[i] *= x[j];
      }
      if (i != 0) {
        f[i] *= 1.0 - x[m - (i + 1)];
      }
    }
  }

  /** The objectives of DTLZ3, whose front is the unit sphere. */
  protected static void sphericalObjectives(double[] x, double g, double[] f) {
    int m = f.length;
    for (int i = 0; i < m; i++) {
      f[i] = 1.0 + g;
      for (int j = 0; j < m - (i + 1); j++) {
        f[i] *= Math.cos(x[j] * 0.5 * Math.PI);
      }
      if (i != 0) {
        f[i] *= Math.sin(x[m - (i + 1)] * 0.5 * Math.PI);
      }
    }
  }

  /** The constraint cos(a pi y) - b &gt;= 0 on a position variable or on g. */
  protected double cosineConstraint(double y) {
    return Math.cos(a * Math.PI * y) - b;
  }
}
