package org.uma.jmetal.problem.multiobjective.df;

/**
 * Problem DF11 of the CEC 2018 Competition on Dynamic Multiobjective Optimisation (see {@link DF}),
 * with three objectives:
 *
 * <pre>
 * f_1 = g sin(y_1),  f_2 = g sin(y_2) cos(y_1),  f_3 = g cos(y_2) cos(y_1)
 * g = 1 + G + sum_{i=3..n} (x_i - 0.5 G x_1)^2,  y_j = pi G / 6 + (pi / 2 - pi G / 3) x_j
 * G = |sin(0.5 pi t)|
 * x in [0, 1]^n
 * </pre>
 */
@SuppressWarnings("serial")
public class DF11 extends DF {

  /** Creates the problem with 10 variables, tau_t = 10 and n_t = 10. */
  public DF11() {
    this(DEFAULT_NUMBER_OF_VARIABLES);
  }

  /**
   * Creates the problem with tau_t = 10 and n_t = 10.
   *
   * @param numberOfVariables the number of variables, at least 3
   */
  public DF11(int numberOfVariables) {
    this(numberOfVariables, DEFAULT_FREQUENCY_OF_CHANGE, DEFAULT_SEVERITY_OF_CHANGE);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 3
   * @param frequencyOfChange tau_t, positive
   * @param severityOfChange n_t, positive
   */
  public DF11(int numberOfVariables, int frequencyOfChange, int severityOfChange) {
    super(
        "DF11",
        numberOfVariables,
        3,
        frequencyOfChange,
        severityOfChange,
        0,
        0.0,
        1.0,
        0.0,
        1.0);
  }

  @Override
  protected void evaluate(double[] x, double t, double[] f) {
    double bigG = Math.abs(Math.sin(0.5 * Math.PI * t));
    double g = 1.0 + bigG + sumOfSquaredDifferences(x, 2, 0.5 * bigG * x[0]);
    double y1 = Math.PI * bigG / 6.0 + (Math.PI / 2.0 - Math.PI * bigG / 3.0) * x[0];
    double y2 = Math.PI * bigG / 6.0 + (Math.PI / 2.0 - Math.PI * bigG / 3.0) * x[1];
    f[0] = g * Math.sin(y1);
    f[1] = g * Math.sin(y2) * Math.cos(y1);
    f[2] = g * Math.cos(y2) * Math.cos(y1);
  }
}
