package org.uma.jmetal.problem.multiobjective.df;

/**
 * Problem DF3 of the CEC 2018 Competition on Dynamic Multiobjective Optimisation (see {@link DF}),
 * with two objectives:
 *
 * <pre>
 * f_1 = x_1,  f_2 = g (1 - (x_1 / g)^H)
 * g = 1 + sum_{i=2..n} (x_i - G - x_1^H)^2,  G = sin(0.5 pi t),  H = G + 1.5
 * x_1 in [0, 1], x_i in [-1, 2]
 * </pre>
 */
@SuppressWarnings("serial")
public class DF3 extends DF {

  /** Creates the problem with 10 variables, tau_t = 10 and n_t = 10. */
  public DF3() {
    this(DEFAULT_NUMBER_OF_VARIABLES);
  }

  /**
   * Creates the problem with tau_t = 10 and n_t = 10.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public DF3(int numberOfVariables) {
    this(numberOfVariables, DEFAULT_FREQUENCY_OF_CHANGE, DEFAULT_SEVERITY_OF_CHANGE);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   * @param frequencyOfChange tau_t, positive
   * @param severityOfChange n_t, positive
   */
  public DF3(int numberOfVariables, int frequencyOfChange, int severityOfChange) {
    super(
        "DF3",
        numberOfVariables,
        2,
        frequencyOfChange,
        severityOfChange,
        1,
        0.0,
        1.0,
        -1.0,
        2.0);
  }

  @Override
  protected void evaluate(double[] x, double t, double[] f) {
    double bigG = Math.sin(0.5 * Math.PI * t);
    double h = bigG + 1.5;
    double g = 1.0 + sumOfSquaredDifferences(x, 1, bigG + Math.pow(x[0], h));
    f[0] = x[0];
    f[1] = g * (1.0 - Math.pow(x[0] / g, h));
  }
}
