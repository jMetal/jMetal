package org.uma.jmetal.problem.multiobjective.df;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Base class of the dynamic problems DF1-DF14 of the CEC 2018 Competition on Dynamic Multiobjective
 * Optimisation: S. Jiang, S. Yang, X. Yao, K. C. Tan, M. Kaiser and N. Krasnogor, "Benchmark
 * Problems for CEC'2018 Competition on Dynamic Multiobjective Optimisation", technical report,
 * Newcastle University, 2017. DF1-DF9 have two objectives and DF10-DF14 three.
 *
 * <p>The objectives depend on the time instant
 *
 * <pre>
 * t = (1 / n_t) floor(tau / tau_t)
 * </pre>
 *
 * <p>where tau is a counter given with {@link #update(Integer)} (the generation of the algorithm in
 * the competition, or any external clock), tau_t is the frequency of change and n_t the severity of
 * change. The defaults are the recommended settings of the report: 10 variables, tau_t = 10 and n_t
 * = 10. The official MATLAB code (cec2018_DF.m) also delays the first change to generation T0 + 1 =
 * 51, using max(tau + tau_t - (T0 + 1), 0) instead of tau; that is a setting of the competition, and
 * the counter given to these classes can include it. The problem is marked as changed only when t
 * changes, not every time the counter is updated.
 *
 * <p>The classes follow the definitions of the report. The official MATLAB code and pymoo differ in
 * some of them:
 *
 * <table>
 *   <caption>Differences between the report, the official MATLAB code and pymoo</caption>
 *   <tr><th></th><th>Report (these classes)</th><th>MATLAB code</th><th>pymoo</th></tr>
 *   <tr>
 *     <td>DF10, g</td><td>sin(2 pi (x_1 + x_2))</td><td>sin(4 pi (x_1 + x_2))</td>
 *     <td>sin(4 pi (x_1 + x_2))</td>
 *   </tr>
 *   <tr>
 *     <td>DF12, k and r</td><td>k = floor(10 sin(pi t)), r = 1 - mod(k, 2)</td>
 *     <td>k = 10 sin(pi t), r = 1</td><td>k = 10 sin(pi t), r = 1</td>
 *   </tr>
 *   <tr>
 *     <td>DF13, f_3</td><td>g multiplies the whole sum</td>
 *     <td>g multiplies only its first term</td><td>g multiplies the whole sum</td>
 *   </tr>
 * </table>
 *
 * <p>The implementation of jMetalSP, from which these problems come, also differed from the report:
 * f_1 of DF2 was x_1 instead of x_r; DF5, DF7 and DF9 added 2t, and DF8 4t, to their objectives;
 * DF8 used beta_t = 100 G(t)^2 (the report recommends 1 and leaves 100 G(t)^2 for diversity
 * testing); G(t) of DF11 had no absolute value; and f_1 and f_2 of DF13 were not squared.
 */
@SuppressWarnings("serial")
public abstract class DF extends AbstractDoubleProblem
    implements DynamicProblem<DoubleSolution, Integer> {
  public static final int DEFAULT_NUMBER_OF_VARIABLES = 10;
  public static final int DEFAULT_FREQUENCY_OF_CHANGE = 10;
  public static final int DEFAULT_SEVERITY_OF_CHANGE = 10;

  private final int frequencyOfChange;
  private final int severityOfChange;
  private volatile double time = 0.0;
  private volatile boolean changed = false;

  /**
   * Checks the parameters and sets the name, dimensions and bounds: the first {@code
   * numberOfFirstVariables} variables in [firstLower, firstUpper] and the rest in [restLower,
   * restUpper].
   *
   * @param name the name of the problem
   * @param numberOfVariables the number of variables, at least the number of objectives
   * @param numberOfObjectives the number of objectives, 2 or 3
   * @param frequencyOfChange tau_t, positive
   * @param severityOfChange n_t, positive
   */
  protected DF(
      String name,
      int numberOfVariables,
      int numberOfObjectives,
      int frequencyOfChange,
      int severityOfChange,
      int numberOfFirstVariables,
      double firstLower,
      double firstUpper,
      double restLower,
      double restUpper) {
    Check.that(
        numberOfVariables >= numberOfObjectives,
        () ->
            name
                + " needs at least "
                + numberOfObjectives
                + " variables ("
                + numberOfVariables
                + ")");
    Check.that(
        frequencyOfChange > 0,
        () -> name + ": the frequency of change must be positive (" + frequencyOfChange + ")");
    Check.that(
        severityOfChange > 0,
        () -> name + ": the severity of change must be positive (" + severityOfChange + ")");
    this.frequencyOfChange = frequencyOfChange;
    this.severityOfChange = severityOfChange;
    name(name);
    numberOfObjectives(numberOfObjectives);

    List<Double> lowerBounds = new ArrayList<>(numberOfVariables);
    List<Double> upperBounds = new ArrayList<>(numberOfVariables);
    for (int i = 0; i < numberOfVariables; i++) {
      boolean first = i < numberOfFirstVariables;
      lowerBounds.add(first ? firstLower : restLower);
      upperBounds.add(first ? firstUpper : restUpper);
    }
    variableBounds(lowerBounds, upperBounds);
  }

  /** Sets the time instant from the counter tau, and marks the problem as changed if it changes. */
  @Override
  public void update(Integer tau) {
    Check.notNull(tau);
    Check.that(tau >= 0, () -> "The counter must not be negative (" + tau + ")");
    double newTime = Math.floor(tau / (double) frequencyOfChange) / severityOfChange;
    if (newTime != time) {
      time = newTime;
      setChanged();
    }
  }

  /** The current time instant t. */
  public double time() {
    return time;
  }

  public int frequencyOfChange() {
    return frequencyOfChange;
  }

  public int severityOfChange() {
    return severityOfChange;
  }

  @Override
  public boolean hasChanged() {
    return changed;
  }

  @Override
  public void setChanged() {
    changed = true;
  }

  @Override
  public void clearChanged() {
    changed = false;
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = solution.variables().stream().mapToDouble(Double::doubleValue).toArray();
    evaluate(x, time, solution.objectives());
    return solution;
  }

  /**
   * Computes the objectives.
   *
   * @param x the variables
   * @param t the time instant
   * @param f the array where the objectives are written
   */
  protected abstract void evaluate(double[] x, double t, double[] f);

  /** The sum of (x_i - value)^2 for the variables from {@code from} (0-based) to the last one. */
  protected static double sumOfSquaredDifferences(double[] x, int from, double value) {
    double sum = 0.0;
    for (int i = from; i < x.length; i++) {
      sum += (x[i] - value) * (x[i] - value);
    }
    return sum;
  }
}
