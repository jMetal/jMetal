package org.uma.jmetal.problem.multiobjective.df;

/**
 * Problem DF13 of the CEC 2018 Competition on Dynamic Multiobjective Optimisation (see {@link DF}),
 * with three objectives:
 *
 * <pre>
 * f_1 = g cos(0.5 pi x_1)^2,  f_2 = g cos(0.5 pi x_2)^2
 * f_3 = g sum_{j=1,2} (sin(0.5 pi x_j)^2 + sin(0.5 pi x_j) cos(p pi x_j)^2)
 * g = 1 + sum_{i=3..n} (x_i - G)^2,  G = sin(0.5 pi t),  p = floor(6 G)
 * x_1, x_2 in [0, 1], x_i in [-1, 1]
 * </pre>
 *
 * <p>In the official MATLAB code g multiplies only the first term of f_3; this class follows the
 * report, as pymoo.
 */
@SuppressWarnings("serial")
public class DF13 extends DF {

  /** Creates the problem with 10 variables, tau_t = 10 and n_t = 10. */
  public DF13() {
    this(DEFAULT_NUMBER_OF_VARIABLES);
  }

  /**
   * Creates the problem with tau_t = 10 and n_t = 10.
   *
   * @param numberOfVariables the number of variables, at least 3
   */
  public DF13(int numberOfVariables) {
    this(numberOfVariables, DEFAULT_FREQUENCY_OF_CHANGE, DEFAULT_SEVERITY_OF_CHANGE);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 3
   * @param frequencyOfChange tau_t, positive
   * @param severityOfChange n_t, positive
   */
  public DF13(int numberOfVariables, int frequencyOfChange, int severityOfChange) {
    super(
        "DF13",
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
    double bigG = Math.sin(0.5 * Math.PI * t);
    double p = Math.floor(6.0 * bigG);
    double g = 1.0 + sumOfSquaredDifferences(x, 2, bigG);
    double sum = 0.0;
    for (int j = 0; j < 2; j++) {
      double s = Math.sin(0.5 * Math.PI * x[j]);
      double c = Math.cos(p * Math.PI * x[j]);
      sum += s * s + s * c * c;
    }
    f[0] = g * Math.pow(Math.cos(0.5 * Math.PI * x[0]), 2);
    f[1] = g * Math.pow(Math.cos(0.5 * Math.PI * x[1]), 2);
    f[2] = g * sum;
  }
}
