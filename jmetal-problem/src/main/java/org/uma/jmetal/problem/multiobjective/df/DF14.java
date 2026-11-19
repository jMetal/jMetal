package org.uma.jmetal.problem.multiobjective.df;

/**
 * Problem DF14 of the CEC 2018 Competition on Dynamic Multiobjective Optimisation (see {@link DF}),
 * with three objectives:
 *
 * <pre>
 * f_1 = g (1 - y + 0.05 sin(6 pi y))
 * f_2 = g (1 - x_2 + 0.05 sin(6 pi x_2)) (y + 0.05 sin(6 pi y))
 * f_3 = g (x_2 + 0.05 sin(6 pi x_2)) (y + 0.05 sin(6 pi y))
 * g = 1 + sum_{i=3..n} (x_i - G)^2,  y = 0.5 + G (x_1 - 0.5),  G = sin(0.5 pi t)
 * x_1, x_2 in [0, 1], x_i in [-1, 1]
 * </pre>
 */
@SuppressWarnings("serial")
public class DF14 extends DF {

  /** Creates the problem with 10 variables, tau_t = 10 and n_t = 10. */
  public DF14() {
    this(DEFAULT_NUMBER_OF_VARIABLES);
  }

  /**
   * Creates the problem with tau_t = 10 and n_t = 10.
   *
   * @param numberOfVariables the number of variables, at least 3
   */
  public DF14(int numberOfVariables) {
    this(numberOfVariables, DEFAULT_FREQUENCY_OF_CHANGE, DEFAULT_SEVERITY_OF_CHANGE);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 3
   * @param frequencyOfChange tau_t, positive
   * @param severityOfChange n_t, positive
   */
  public DF14(int numberOfVariables, int frequencyOfChange, int severityOfChange) {
    super(
        "DF14",
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
    double g = 1.0 + sumOfSquaredDifferences(x, 2, bigG);
    double y = 0.5 + bigG * (x[0] - 0.5);
    double hy = y + 0.05 * Math.sin(6.0 * Math.PI * y);
    f[0] = g * (1.0 - y + 0.05 * Math.sin(6.0 * Math.PI * y));
    f[1] = g * (1.0 - x[1] + 0.05 * Math.sin(6.0 * Math.PI * x[1])) * hy;
    f[2] = g * (x[1] + 0.05 * Math.sin(6.0 * Math.PI * x[1])) * hy;
  }
}
