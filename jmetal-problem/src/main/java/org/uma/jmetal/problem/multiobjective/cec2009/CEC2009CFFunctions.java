package org.uma.jmetal.problem.multiobjective.cec2009;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * The set-up and the functions shared by the constrained problems CF1-CF10 of the CEC 2009
 * competition (Q. Zhang, A. Zhou, S. Zhao, P. N. Suganthan, W. Liu and S. Tiwari, "Multiobjective
 * optimization test instances for the CEC 2009 special session and competition", Technical Report
 * CES-487, University of Essex and Nanyang Technological University, 2008).
 */
final class CEC2009CFFunctions {

  /** The threshold 1.5 - 0.75 sqrt(2) = 3/2 (1 - sqrt(2)/2) of the term h2 of CF4 and CF5. */
  private static final double H2_THRESHOLD = 1.5 - 0.75 * Math.sqrt(2.0);

  private CEC2009CFFunctions() {}

  /**
   * Checks the number of variables and sets the name, the dimensions and the bounds of a problem:
   * the position variables (one for two objectives, two for three) in [0, 1] and the other ones in
   * [lowerBound, upperBound].
   *
   * @param problem the problem
   * @param name its name
   * @param numberOfVariables the number of variables
   * @param minimumNumberOfVariables the smallest number of variables that the problem allows
   * @param numberOfObjectives the number of objectives (2 or 3)
   * @param numberOfConstraints the number of constraints
   * @param lowerBound the lower bound of the variables that are not position variables
   * @param upperBound the upper bound of the variables that are not position variables
   */
  static void setUp(
      AbstractDoubleProblem problem,
      String name,
      int numberOfVariables,
      int minimumNumberOfVariables,
      int numberOfObjectives,
      int numberOfConstraints,
      double lowerBound,
      double upperBound) {
    Check.that(
        numberOfVariables >= minimumNumberOfVariables,
        () ->
            name
                + " needs at least "
                + minimumNumberOfVariables
                + " variables ("
                + numberOfVariables
                + ")");
    problem.name(name);
    problem.numberOfObjectives(numberOfObjectives);
    problem.numberOfConstraints(numberOfConstraints);

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Double> upperLimit = new ArrayList<>(numberOfVariables);
    for (int i = 0; i < numberOfVariables; i++) {
      boolean position = i < numberOfObjectives - 1;
      lowerLimit.add(position ? 0.0 : lowerBound);
      upperLimit.add(position ? 1.0 : upperBound);
    }
    problem.variableBounds(lowerLimit, upperLimit);
  }

  /** The variables of a solution as an array. */
  static double[] variables(DoubleSolution solution) {
    double[] x = new double[solution.variables().size()];
    for (int i = 0; i < x.length; i++) {
      x[i] = solution.variables().get(i);
    }
    return x;
  }

  /**
   * The constraint of CF2 and CF4 from its argument t: t / (1 + e^(4 |t|)), with the sign of t.
   */
  static double sigmoid(double t) {
    return t / (1.0 + Math.exp(4.0 * Math.abs(t)));
  }

  /**
   * The term of x2 in CF4 and CF5: |t| if t &lt; 1.5 - 0.75 sqrt(2), and 0.125 + (t - 1)^2
   * otherwise (a continuous function with a local minimum of 0.125 at t = 1).
   */
  static double h2(double t) {
    return t < H2_THRESHOLD ? Math.abs(t) : 0.125 + (t - 1.0) * (t - 1.0);
  }

  /** sign(value) sqrt(|value|), the bound of y2 and y4 in the constraints of CF6 and CF7. */
  static double signedSquareRoot(double value) {
    return value > 0.0 ? Math.sqrt(value) : -Math.sqrt(-value);
  }

  /**
   * The objectives of CF8, CF9 and CF10:
   *
   * <pre>
   * f1 = cos(0.5 x1 pi) cos(0.5 x2 pi) + 2 / |J1| sum_{j in J1} h(y_j)
   * f2 = cos(0.5 x1 pi) sin(0.5 x2 pi) + 2 / |J2| sum_{j in J2} h(y_j)
   * f3 = sin(0.5 x1 pi)                + 2 / |J3| sum_{j in J3} h(y_j)
   * y_j = x_j - 2 x2 sin(2 pi x1 + j pi / n)
   * </pre>
   *
   * <p>where J1, J2 and J3 are the j in [3, n] with j - 1, j - 2 and j multiple of 3, and h(t) is
   * t^2 (CF8, CF9) or 4 t^2 - cos(8 pi t) + 1 (CF10).
   *
   * @param x the variables
   * @param multimodal whether h(t) is 4 t^2 - cos(8 pi t) + 1 (CF10) instead of t^2
   * @param f the array where the three objectives are written
   */
  static void sphereObjectives(double[] x, boolean multimodal, double[] f) {
    int n = x.length;
    double[] sum = new double[3];
    int[] count = new int[3];
    for (int j = 3; j <= n; j++) {
      double yj = x[j - 1] - 2.0 * x[1] * Math.sin(2.0 * Math.PI * x[0] + j * Math.PI / n);
      double hj = multimodal ? 4.0 * yj * yj - Math.cos(8.0 * Math.PI * yj) + 1.0 : yj * yj;
      // J1 (j % 3 == 1) adds to f1, J2 (j % 3 == 2) to f2 and J3 (j % 3 == 0) to f3
      int objective = (j + 2) % 3;
      sum[objective] += hj;
      count[objective]++;
    }
    double cosine = Math.cos(0.5 * Math.PI * x[0]);
    f[0] = cosine * Math.cos(0.5 * Math.PI * x[1]) + 2.0 * sum[0] / count[0];
    f[1] = cosine * Math.sin(0.5 * Math.PI * x[1]) + 2.0 * sum[1] / count[1];
    f[2] = Math.sin(0.5 * Math.PI * x[0]) + 2.0 * sum[2] / count[2];
  }

  /**
   * The constraint of CF8, CF9 and CF10:
   *
   * <pre>
   * c = (f1^2 + f2^2) / (1 - f3^2) - a s(N pi ((f1^2 - f2^2) / (1 - f3^2) + 1)) - 1
   * </pre>
   *
   * <p>where s is |sin| (CF8) or sin (CF9, CF10). When f3 = 1 the code of the competition divides
   * by zero and returns NaN, a value that the evaluators of jMetal reject; the constraint is then 0
   * (satisfied). This happens at the top (0, 0, 1) of the Pareto front, where all its parts meet,
   * and, by rounding, with x1 within about 1e-8 of 1 and the distance terms of f3 below about
   * 1e-16.
   *
   * @param f the three objectives
   * @param n the constant N
   * @param a the constant a
   * @param absoluteSine whether the sine is taken in absolute value
   * @return the constraint (satisfied if &gt;= 0)
   */
  static double sphereConstraint(double[] f, double n, double a, boolean absoluteSine) {
    double denominator = 1.0 - f[2] * f[2];
    if (denominator == 0.0) {
      return 0.0;
    }
    double sine = Math.sin(n * Math.PI * ((f[0] * f[0] - f[1] * f[1]) / denominator + 1.0));
    return (f[0] * f[0] + f[1] * f[1]) / denominator
        - a * (absoluteSine ? Math.abs(sine) : sine)
        - 1.0;
  }
}
