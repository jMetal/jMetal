package org.uma.jmetal.problem.multiobjective.df;

/**
 * Problem DF2 of the CEC 2018 Competition on Dynamic Multiobjective Optimisation (see {@link DF}),
 * with two objectives:
 *
 * <pre>
 * f_1 = x_r,  f_2 = g (1 - (x_r / g)^0.5)
 * g = 1 + sum_{i != r} (x_i - G)^2,  G = |sin(0.5 pi t)|,  r = 1 + floor((n - 1) G)
 * x in [0, 1]^n
 * </pre>
 */
@SuppressWarnings("serial")
public class DF2 extends DF {

  /** Creates the problem with 10 variables, tau_t = 10 and n_t = 10. */
  public DF2() {
    this(DEFAULT_NUMBER_OF_VARIABLES);
  }

  /**
   * Creates the problem with tau_t = 10 and n_t = 10.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public DF2(int numberOfVariables) {
    this(numberOfVariables, DEFAULT_FREQUENCY_OF_CHANGE, DEFAULT_SEVERITY_OF_CHANGE);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   * @param frequencyOfChange tau_t, positive
   * @param severityOfChange n_t, positive
   */
  public DF2(int numberOfVariables, int frequencyOfChange, int severityOfChange) {
    super(
        "DF2",
        numberOfVariables,
        2,
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
    int r = (int) Math.floor((x.length - 1) * bigG);
    double g = 1.0 + sumOfSquaredDifferences(x, 0, bigG) - (x[r] - bigG) * (x[r] - bigG);
    f[0] = x[r];
    f[1] = g * (1.0 - Math.sqrt(x[r] / g));
  }
}
