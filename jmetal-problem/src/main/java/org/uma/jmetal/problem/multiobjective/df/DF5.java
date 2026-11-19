package org.uma.jmetal.problem.multiobjective.df;

/**
 * Problem DF5 of the CEC 2018 Competition on Dynamic Multiobjective Optimisation (see {@link DF}),
 * with two objectives:
 *
 * <pre>
 * f_1 = g (x_1 + 0.02 sin(w pi x_1)),  f_2 = g (1 - x_1 + 0.02 sin(w pi x_1))
 * g = 1 + sum_{i=2..n} (x_i - G)^2,  G = sin(0.5 pi t),  w = floor(10 G)
 * x_1 in [0, 1], x_i in [-1, 1]
 * </pre>
 */
@SuppressWarnings("serial")
public class DF5 extends DF {

  /** Creates the problem with 10 variables, tau_t = 10 and n_t = 10. */
  public DF5() {
    this(DEFAULT_NUMBER_OF_VARIABLES);
  }

  /**
   * Creates the problem with tau_t = 10 and n_t = 10.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public DF5(int numberOfVariables) {
    this(numberOfVariables, DEFAULT_FREQUENCY_OF_CHANGE, DEFAULT_SEVERITY_OF_CHANGE);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   * @param frequencyOfChange tau_t, positive
   * @param severityOfChange n_t, positive
   */
  public DF5(int numberOfVariables, int frequencyOfChange, int severityOfChange) {
    super(
        "DF5",
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
    double w = Math.floor(10.0 * bigG);
    double g = 1.0 + sumOfSquaredDifferences(x, 1, bigG);
    double s = 0.02 * Math.sin(w * Math.PI * x[0]);
    f[0] = g * (x[0] + s);
    f[1] = g * (1.0 - x[0] + s);
  }
}
