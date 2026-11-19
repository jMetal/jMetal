package org.uma.jmetal.problem.multiobjective.df;

/**
 * Problem DF6 of the CEC 2018 Competition on Dynamic Multiobjective Optimisation (see {@link DF}),
 * with two objectives:
 *
 * <pre>
 * f_1 = g (x_1 + 0.1 sin(3 pi x_1))^a,  f_2 = g (1 - x_1 + 0.1 sin(3 pi x_1))^a
 * g = 1 + sum_{i=2..n} (|G| y_i^2 - 10 cos(2 pi y_i) + 10),  y_i = x_i - G
 * G = sin(0.5 pi t),  a = 0.2 + 2.8 |G|
 * x_1 in [0, 1], x_i in [-1, 1]
 * </pre>
 */
@SuppressWarnings("serial")
public class DF6 extends DF {

  /** Creates the problem with 10 variables, tau_t = 10 and n_t = 10. */
  public DF6() {
    this(DEFAULT_NUMBER_OF_VARIABLES);
  }

  /**
   * Creates the problem with tau_t = 10 and n_t = 10.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public DF6(int numberOfVariables) {
    this(numberOfVariables, DEFAULT_FREQUENCY_OF_CHANGE, DEFAULT_SEVERITY_OF_CHANGE);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   * @param frequencyOfChange tau_t, positive
   * @param severityOfChange n_t, positive
   */
  public DF6(int numberOfVariables, int frequencyOfChange, int severityOfChange) {
    super(
        "DF6",
        numberOfVariables,
        2,
        frequencyOfChange,
        severityOfChange,
        1,
        0.0,
        1.0,
        -1.0,
        1.0);
  }

  @Override
  protected void evaluate(double[] x, double t, double[] f) {
    double bigG = Math.sin(0.5 * Math.PI * t);
    double a = 0.2 + 2.8 * Math.abs(bigG);
    double g = 1.0;
    for (int i = 1; i < x.length; i++) {
      double y = x[i] - bigG;
      g += Math.abs(bigG) * y * y - 10.0 * Math.cos(2.0 * Math.PI * y) + 10.0;
    }
    double s = 0.1 * Math.sin(3.0 * Math.PI * x[0]);
    f[0] = g * Math.pow(x[0] + s, a);
    f[1] = g * Math.pow(1.0 - x[0] + s, a);
  }
}
