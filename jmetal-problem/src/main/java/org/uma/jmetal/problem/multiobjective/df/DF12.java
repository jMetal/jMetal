package org.uma.jmetal.problem.multiobjective.df;

/**
 * Problem DF12 of the CEC 2018 Competition on Dynamic Multiobjective Optimisation (see {@link DF}),
 * with three objectives:
 *
 * <pre>
 * f_1 = g cos(0.5 pi x_1) cos(0.5 pi x_2)
 * f_2 = g cos(0.5 pi x_1) sin(0.5 pi x_2)
 * f_3 = g sin(0.5 pi x_1)
 * g = 1 + sum_{i=3..n} (x_i - sin(t x_1))^2 + |prod_{j=1,2} sin(floor(k (2 x_j - r)) pi / 2)|
 * k = floor(10 sin(pi t)),  r = 1 - mod(k, 2)
 * x_1, x_2 in [0, 1], x_i in [-1, 1]
 * </pre>
 *
 * <p>The official MATLAB code and pymoo use k = 10 sin(pi t), without the floor, and r = 1; this
 * class follows the report. The absolute value of the product, which keeps g at least 1, is that of
 * the code.
 */
@SuppressWarnings("serial")
public class DF12 extends DF {

  /** Creates the problem with 10 variables, tau_t = 10 and n_t = 10. */
  public DF12() {
    this(DEFAULT_NUMBER_OF_VARIABLES);
  }

  /**
   * Creates the problem with tau_t = 10 and n_t = 10.
   *
   * @param numberOfVariables the number of variables, at least 3
   */
  public DF12(int numberOfVariables) {
    this(numberOfVariables, DEFAULT_FREQUENCY_OF_CHANGE, DEFAULT_SEVERITY_OF_CHANGE);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 3
   * @param frequencyOfChange tau_t, positive
   * @param severityOfChange n_t, positive
   */
  public DF12(int numberOfVariables, int frequencyOfChange, int severityOfChange) {
    super(
        "DF12",
        numberOfVariables,
        3,
        frequencyOfChange,
        severityOfChange,
        2,
        0.0,
        1.0,
        -1.0,
        1.0);
  }

  @Override
  protected void evaluate(double[] x, double t, double[] f) {
    double k = Math.floor(10.0 * Math.sin(Math.PI * t));
    double r = 1.0 - Math.floorMod((long) k, 2L);
    double product =
        Math.sin(Math.floor(k * (2.0 * x[0] - r)) * Math.PI / 2.0)
            * Math.sin(Math.floor(k * (2.0 * x[1] - r)) * Math.PI / 2.0);
    double g = 1.0 + sumOfSquaredDifferences(x, 2, Math.sin(t * x[0])) + Math.abs(product);
    f[0] = g * Math.cos(0.5 * Math.PI * x[1]) * Math.cos(0.5 * Math.PI * x[0]);
    f[1] = g * Math.sin(0.5 * Math.PI * x[1]) * Math.cos(0.5 * Math.PI * x[0]);
    f[2] = g * Math.sin(0.5 * Math.PI * x[0]);
  }
}
