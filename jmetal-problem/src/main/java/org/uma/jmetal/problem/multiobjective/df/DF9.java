package org.uma.jmetal.problem.multiobjective.df;

/**
 * Problem DF9 of the CEC 2018 Competition on Dynamic Multiobjective Optimisation (see {@link DF}),
 * with two objectives:
 *
 * <pre>
 * f_1 = g (x_1 + m),  f_2 = g (1 - x_1 + m),  m = max(0, (0.1 + 0.5 / N) sin(2 N pi x_1))
 * g = 1 + sum_{i=2..n} (x_i - cos(4 t + x_1 + x_{i-1}))^2,  N = 1 + floor(10 |sin(0.5 pi t)|)
 * x_1 in [0, 1], x_i in [-1, 1]
 * </pre>
 */
@SuppressWarnings("serial")
public class DF9 extends DF {

  /** Creates the problem with 10 variables, tau_t = 10 and n_t = 10. */
  public DF9() {
    this(DEFAULT_NUMBER_OF_VARIABLES);
  }

  /**
   * Creates the problem with tau_t = 10 and n_t = 10.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public DF9(int numberOfVariables) {
    this(numberOfVariables, DEFAULT_FREQUENCY_OF_CHANGE, DEFAULT_SEVERITY_OF_CHANGE);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   * @param frequencyOfChange tau_t, positive
   * @param severityOfChange n_t, positive
   */
  public DF9(int numberOfVariables, int frequencyOfChange, int severityOfChange) {
    super(
        "DF9",
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
    double bigN = 1.0 + Math.floor(10.0 * Math.abs(Math.sin(0.5 * Math.PI * t)));
    double g = 1.0;
    for (int i = 1; i < x.length; i++) {
      double d = x[i] - Math.cos(4.0 * t + x[0] + x[i - 1]);
      g += d * d;
    }
    double m = Math.max(0.0, (0.1 + 0.5 / bigN) * Math.sin(2.0 * bigN * Math.PI * x[0]));
    f[0] = g * (x[0] + m);
    f[1] = g * (1.0 - x[0] + m);
  }
}
