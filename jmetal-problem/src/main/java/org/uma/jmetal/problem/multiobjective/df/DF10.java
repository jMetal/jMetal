package org.uma.jmetal.problem.multiobjective.df;

/**
 * Problem DF10 of the CEC 2018 Competition on Dynamic Multiobjective Optimisation (see {@link DF}),
 * with three objectives:
 *
 * <pre>
 * f_1 = g sin(0.5 pi x_1)^H
 * f_2 = g (sin(0.5 pi x_2) cos(0.5 pi x_1))^H
 * f_3 = g (cos(0.5 pi x_2) cos(0.5 pi x_1))^H
 * g = 1 + sum_{i=3..n} (x_i - sin(2 pi (x_1 + x_2)) / (1 + |G|))^2
 * G = sin(0.5 pi t),  H = 2.25 + 2 cos(0.5 pi t)
 * x_1, x_2 in [0, 1], x_i in [-1, 1]
 * </pre>
 *
 * <p>The official MATLAB code and pymoo use sin(4 pi (x_1 + x_2)); this class follows the report.
 */
@SuppressWarnings("serial")
public class DF10 extends DF {

  /** Creates the problem with 10 variables, tau_t = 10 and n_t = 10. */
  public DF10() {
    this(DEFAULT_NUMBER_OF_VARIABLES);
  }

  /**
   * Creates the problem with tau_t = 10 and n_t = 10.
   *
   * @param numberOfVariables the number of variables, at least 3
   */
  public DF10(int numberOfVariables) {
    this(numberOfVariables, DEFAULT_FREQUENCY_OF_CHANGE, DEFAULT_SEVERITY_OF_CHANGE);
  }

  /**
   * Creates the problem.
   *
   * @param numberOfVariables the number of variables, at least 3
   * @param frequencyOfChange tau_t, positive
   * @param severityOfChange n_t, positive
   */
  public DF10(int numberOfVariables, int frequencyOfChange, int severityOfChange) {
    super(
        "DF10",
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
    double h = 2.25 + 2.0 * Math.cos(0.5 * Math.PI * t);
    double g =
        1.0
            + sumOfSquaredDifferences(
                x, 2, Math.sin(2.0 * Math.PI * (x[0] + x[1])) / (1.0 + Math.abs(bigG)));
    f[0] = g * Math.pow(Math.sin(0.5 * Math.PI * x[0]), h);
    f[1] = g * Math.pow(Math.sin(0.5 * Math.PI * x[1]) * Math.cos(0.5 * Math.PI * x[0]), h);
    f[2] = g * Math.pow(Math.cos(0.5 * Math.PI * x[1]) * Math.cos(0.5 * Math.PI * x[0]), h);
  }
}
