package org.uma.jmetal.problem.multiobjective.df;

/**
 * Problem DF4 of the CEC 2018 Competition on Dynamic Multiobjective Optimisation (see {@link DF}),
 * with two objectives:
 *
 * <pre>
 * f_1 = g |x_1 - a|^H,  f_2 = g |x_1 - a - b|^H
 * g = 1 + sum_{i=2..n} (x_i - a (x_1 / c)^2 / i)^2
 * a = sin(0.5 pi t),  b = 1 + |cos(0.5 pi t)|,  c = max(|a|, a + b),  H = 1.5 + a
 * x in [-2, 2]^n
 * </pre>
 */
@SuppressWarnings("serial")
public class DF4 extends DF {

  /** Creates the problem with 10 variables, tau_t = 10 and n_t = 10. */
  public DF4() {
    this(DEFAULT_NUMBER_OF_VARIABLES);
  }

  /**
   * Creates the problem with tau_t = 10 and n_t = 10.
   *
   * @param numberOfVariables the number of variables, at least 2
   */
  public DF4(int numberOfVariables) {
    this(numberOfVariables, DEFAULT_FREQUENCY_OF_CHANGE, DEFAULT_SEVERITY_OF_CHANGE);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 2
   * @param frequencyOfChange tau_t, positive
   * @param severityOfChange n_t, positive
   */
  public DF4(int numberOfVariables, int frequencyOfChange, int severityOfChange) {
    super(
        "DF4",
        numberOfVariables,
        2,
        frequencyOfChange,
        severityOfChange,
        0,
        -2.0,
        2.0,
        -2.0,
        2.0);
  }

  @Override
  protected void evaluate(double[] x, double t, double[] f) {
    double a = Math.sin(0.5 * Math.PI * t);
    double b = 1.0 + Math.abs(Math.cos(0.5 * Math.PI * t));
    double c = Math.max(Math.abs(a), a + b);
    double h = 1.5 + a;
    double g = 1.0;
    for (int i = 1; i < x.length; i++) {
      double d = x[i] - a * (x[0] / c) * (x[0] / c) / (i + 1);
      g += d * d;
    }
    f[0] = g * Math.pow(Math.abs(x[0] - a), h);
    f[1] = g * Math.pow(Math.abs(x[0] - a - b), h);
  }
}
