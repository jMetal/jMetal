package org.uma.jmetal.problem.multiobjective.df;

/**
 * Problem DF8 of the CEC 2018 Competition on Dynamic Multiobjective Optimisation (see {@link DF}),
 * with two objectives:
 *
 * <pre>
 * f_1 = g (x_1 + 0.1 sin(3 pi x_1)),  f_2 = g (1 - x_1 + 0.1 sin(3 pi x_1))^a
 * g = 1 + sum_{i=2..n} (x_i - G sin(4 pi x_1^beta) / (1 + |G|))^2
 * G = sin(0.5 pi t),  a = 2.25 + 2 cos(2 pi t),  beta = 1
 * x_1 in [0, 1], x_i in [-1, 1]
 * </pre>
 *
 * <p>The report recommends beta = 1, and beta = 100 G^2 if diversity testing is the focus.
 */
@SuppressWarnings("serial")
public class DF8 extends DF {

  /** Creates the problem with 10 variables, tau_t = 10 and n_t = 10. */
  public DF8() {
    this(DEFAULT_NUMBER_OF_VARIABLES);
  }

  /**
   * Creates the problem with tau_t = 10 and n_t = 10.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public DF8(int numberOfVariables) {
    this(numberOfVariables, DEFAULT_FREQUENCY_OF_CHANGE, DEFAULT_SEVERITY_OF_CHANGE);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   * @param frequencyOfChange tau_t, positive
   * @param severityOfChange n_t, positive
   */
  public DF8(int numberOfVariables, int frequencyOfChange, int severityOfChange) {
    super(
        "DF8",
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
    double a = 2.25 + 2.0 * Math.cos(2.0 * Math.PI * t);
    double g =
        1.0
            + sumOfSquaredDifferences(
                x, 1, bigG * Math.sin(4.0 * Math.PI * x[0]) / (1.0 + Math.abs(bigG)));
    double s = 0.1 * Math.sin(3.0 * Math.PI * x[0]);
    f[0] = g * (x[0] + s);
    f[1] = g * Math.pow(1.0 - x[0] + s, a);
  }
}
