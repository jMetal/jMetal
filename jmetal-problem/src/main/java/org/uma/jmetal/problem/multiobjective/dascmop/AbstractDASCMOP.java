package org.uma.jmetal.problem.multiobjective.dascmop;

import java.util.Collections;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Base class of the DAS-CMOP problems, defined in Z. Fan, W. Li, X. Cai, H. Li, C. Wei, Q. Zhang,
 * K. Deb and E. Goodman, "Difficulty Adjustable and Scalable Constrained Multiobjective Test
 * Problem Toolkit", Evolutionary Computation 28(3):339-378, 2020
 * (https://doi.org/10.1162/evco_a_00259).
 *
 * <p>Each objective is a shape function of the position variables plus a distance function g of
 * all the variables, and the constraints depend on a difficulty triplet (eta, zeta, gamma) in [0,
 * 1]^3:
 *
 * <ul>
 *   <li>eta, the diversity-hardness: Type-I constraints, sin(20 pi x1) &gt;= b (and cos(20 pi x2)
 *       &gt;= b with three objectives), with b = 2 eta - 1, cut the front into segments that narrow
 *       as eta grows;
 *   <li>zeta, the feasibility-hardness: the Type-II constraint (e - g)(g - d) &gt;= 0, with d = 0.5
 *       and e = d - ln(zeta), keeps g in [d, e], a band that narrows as zeta grows. With zeta = 0
 *       there is no band (d = 0 and e is infinite); with zeta = 1 the band is a single value, g =
 *       d, taken within 1e-4;
 *   <li>gamma, the convergence-hardness: Type-III constraints put infeasible regions in the
 *       objective space, nine rotated ellipses (two objectives) or four spheres (three objectives)
 *       of a size set by r = gamma / 2.
 * </ul>
 *
 * <p>A triplet with a component of 0 switches that kind of constraint off. The constraint values
 * follow jMetal's convention: a negative value is a violation. With two objectives there are 11
 * constraints (Type-I, Type-II and nine Type-III); with three, 7 (two Type-I, Type-II and four
 * Type-III). The definitions are those of Table 2 of the paper, which PlatEMO follows except in the
 * distance function of DAS-CMOP9, and in that PlatEMO fixes the triplet of each problem.
 */
@SuppressWarnings("serial")
public abstract class AbstractDASCMOP extends AbstractDoubleProblem {

  /** The frequency of the Type-I constraints. */
  private static final double A = 20.0;

  /** The lower bound of the distance function in the Type-II constraint (when zeta is positive). */
  private static final double D = 0.5;

  /** Stands for an infinite upper bound of the Type-II band, as PlatEMO does. */
  private static final double NO_UPPER_BOUND = 1.0e30;

  /** The tolerance of the Type-II constraint with zeta = 1, as PlatEMO's. */
  private static final double EQUALITY_TOLERANCE = 1.0e-4;

  /** The centers, axes and rotation of the Type-III ellipses of the two-objective problems. */
  private static final double[] P = {0.0, 1.0, 0.0, 1.0, 2.0, 0.0, 1.0, 2.0, 3.0};

  private static final double[] Q = {1.5, 0.5, 2.5, 1.5, 0.5, 3.5, 2.5, 1.5, 0.5};
  private static final double SQUARED_A = 0.3;
  private static final double SQUARED_B = 1.2;
  private static final double THETA = -0.25 * Math.PI;

  /** The centers of the Type-III spheres of the three-objective problems. */
  private static final double[][] CENTERS = {
    {1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 1.0}, {1.0 / Math.sqrt(3.0), 1.0 / Math.sqrt(3.0),
      1.0 / Math.sqrt(3.0)}
  };

  private final double eta;
  private final double zeta;
  private final double gamma;
  private final double b;
  private final double d;
  private final double e;
  private final double r;

  /**
   * Creates a DAS-CMOP problem with all its variables in [0, 1].
   *
   * @param name the name of the problem
   * @param numberOfVariables the number of variables, at least the number of objectives
   * @param numberOfObjectives 2 or 3
   * @param eta the diversity-hardness, in [0, 1]
   * @param zeta the feasibility-hardness, in [0, 1]
   * @param gamma the convergence-hardness, in [0, 1]
   */
  protected AbstractDASCMOP(
      String name,
      int numberOfVariables,
      int numberOfObjectives,
      double eta,
      double zeta,
      double gamma) {
    Check.that(
        numberOfObjectives == 2 || numberOfObjectives == 3,
        "The DAS-CMOP problems have two or three objectives");
    Check.that(
        numberOfVariables >= numberOfObjectives,
        () ->
            name
                + " needs at least as many variables ("
                + numberOfVariables
                + ") as objectives ("
                + numberOfObjectives
                + ")");
    Check.valueIsInRange(eta, 0.0, 1.0, "eta");
    Check.valueIsInRange(zeta, 0.0, 1.0, "zeta");
    Check.valueIsInRange(gamma, 0.0, 1.0, "gamma");

    name(name);
    numberOfObjectives(numberOfObjectives);
    numberOfConstraints(numberOfObjectives == 2 ? 11 : 7);
    variableBounds(
        Collections.nCopies(numberOfVariables, 0.0), Collections.nCopies(numberOfVariables, 1.0));

    this.eta = eta;
    this.zeta = zeta;
    this.gamma = gamma;
    this.b = 2.0 * eta - 1.0;
    this.d = zeta == 0.0 ? 0.0 : D;
    this.e = zeta == 0.0 ? NO_UPPER_BOUND : D - Math.log(zeta);
    this.r = 0.5 * gamma;
  }

  /**
   * The distance function g of the problem.
   *
   * @param x the variables
   * @return its value, nonnegative and 0 on the unconstrained Pareto set
   */
  protected abstract double distance(double[] x);

  /**
   * The distance function of DAS-CMOP1-3: g = sum_{j=1..n} (x_j - sin(pi x1 / 2))^2, as in Table 2
   * of the paper (the sum includes x1).
   */
  protected static double sineDistance(double[] x) {
    double target = Math.sin(0.5 * Math.PI * x[0]);
    double result = 0.0;
    for (double value : x) {
      result += (value - target) * (value - target);
    }
    return result;
  }

  /**
   * The distance function of DAS-CMOP4-8: g = (n - k) + sum_{j=k+1..n} ((x_j - 0.5)^2 - cos(20 pi
   * (x_j - 0.5))), with k position variables. Its minimum, 0, is reached with every x_j = 0.5.
   */
  protected static double cosineDistance(double[] x, int positionVariables) {
    double result = x.length - positionVariables;
    for (int j = positionVariables; j < x.length; j++) {
      double y = x[j] - 0.5;
      result += y * y - Math.cos(20.0 * Math.PI * y);
    }
    return result;
  }

  /**
   * The shape functions of the problem: its objectives without the distance function.
   *
   * @param x the variables
   * @return one value per objective
   */
  protected abstract double[] shape(double[] x);

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = new double[numberOfVariables()];
    for (int i = 0; i < x.length; i++) {
      x[i] = solution.variables().get(i);
    }
    double g = distance(x);
    double[] f = shape(x);
    for (int i = 0; i < f.length; i++) {
      f[i] += g;
      solution.objectives()[i] = f[i];
    }
    if (numberOfObjectives() == 2) {
      twoObjectiveConstraints(x, g, f, solution.constraints());
    } else {
      threeObjectiveConstraints(x, g, f, solution.constraints());
    }
    return solution;
  }

  private void twoObjectiveConstraints(double[] x, double g, double[] f, double[] constraints) {
    constraints[0] = Math.sin(A * Math.PI * x[0]) - b;
    constraints[1] = typeII(g);
    for (int k = 0; k < P.length; k++) {
      double u = (f[0] - P[k]) * Math.cos(THETA) - (f[1] - Q[k]) * Math.sin(THETA);
      double v = (f[0] - P[k]) * Math.sin(THETA) + (f[1] - Q[k]) * Math.cos(THETA);
      constraints[2 + k] = u * u / SQUARED_A + v * v / SQUARED_B - r;
    }
  }

  private void threeObjectiveConstraints(double[] x, double g, double[] f, double[] constraints) {
    constraints[0] = Math.sin(A * Math.PI * x[0]) - b;
    constraints[1] = Math.cos(A * Math.PI * x[1]) - b;
    constraints[2] = typeII(g);
    for (int k = 0; k < CENTERS.length; k++) {
      double squaredDistance = 0.0;
      for (int j = 0; j < 3; j++) {
        squaredDistance += (f[j] - CENTERS[k][j]) * (f[j] - CENTERS[k][j]);
      }
      constraints[3 + k] = squaredDistance - r * r;
    }
  }

  /**
   * The Type-II constraint: g in [d, e]. With zeta = 1 the band is the single value d, taken within
   * a tolerance (PlatEMO reverses the sign of this case, which makes feasible what is away from d).
   */
  private double typeII(double g) {
    if (zeta == 1.0) {
      return EQUALITY_TOLERANCE - Math.abs(g - e);
    }
    return (e - g) * (g - d);
  }

  /** The diversity-hardness eta of the problem. */
  public double eta() {
    return eta;
  }

  /** The feasibility-hardness zeta of the problem. */
  public double zeta() {
    return zeta;
  }

  /** The convergence-hardness gamma of the problem. */
  public double gamma() {
    return gamma;
  }
}
