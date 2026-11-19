package org.uma.jmetal.problem.multiobjective.df;

/**
 * Problem DF7 of the CEC 2018 Competition on Dynamic Multiobjective Optimisation (see {@link DF}),
 * with two objectives:
 *
 * <pre>
 * f_1 = g (1 + t) / x_1,  f_2 = g x_1 / (1 + t)
 * g = 1 + sum_{i=2..n} (x_i - 1 / (1 + exp(a (x_1 - 2.5))))^2,  a = 5 cos(0.5 pi t)
 * x_1 in [1, 4], x_i in [0, 1]
 * </pre>
 */
@SuppressWarnings("serial")
public class DF7 extends DF {

  /** Creates the problem with 10 variables, tau_t = 10 and n_t = 10. */
  public DF7() {
    this(DEFAULT_NUMBER_OF_VARIABLES);
  }

  /**
   * Creates the problem with tau_t = 10 and n_t = 10.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public DF7(int numberOfVariables) {
    this(numberOfVariables, DEFAULT_FREQUENCY_OF_CHANGE, DEFAULT_SEVERITY_OF_CHANGE);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   * @param frequencyOfChange tau_t, positive
   * @param severityOfChange n_t, positive
   */
  public DF7(int numberOfVariables, int frequencyOfChange, int severityOfChange) {
    super(
        "DF7",
        numberOfVariables,
        2,
        frequencyOfChange,
        severityOfChange,
        1,
        1.0,
        4.0,
        0.0,
        1.0);
  }

  @Override
  protected void evaluate(double[] x, double t, double[] f) {
    double a = 5.0 * Math.cos(0.5 * Math.PI * t);
    double g = 1.0 + sumOfSquaredDifferences(x, 1, 1.0 / (1.0 + Math.exp(a * (x[0] - 2.5))));
    f[0] = g * (1.0 + t) / x[0];
    f[1] = g * x[0] / (1.0 + t);
  }
}
