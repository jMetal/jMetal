package org.uma.jmetal.problem.multiobjective.fda;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Base class of the dynamic problems FDA1-FDA5 of M. Farina, K. Deb and P. Amato, "Dynamic
 * multiobjective optimization problems: test cases, approximations, and applications", IEEE
 * Transactions on Evolutionary Computation 8(5):425-442, 2004.
 *
 * <p>The objectives depend on the time instant t = (1 / n_T) floor(tau / tau_T), where tau is a
 * counter given with {@link #update(Integer)} (the generation of the algorithm in the paper, or any
 * external clock), tau_T is the number of values of the counter for which t remains fixed and n_T
 * the number of distinct steps in t. The defaults are those of the paper: tau_T = 5 and n_T = 10.
 * The problem is marked as changed only when t changes, not every time the counter is updated.
 *
 * @author Cristóbal Barba <cbarba@lcc.uma.es>
 */
@SuppressWarnings("serial")
public abstract class FDA extends AbstractDoubleProblem
    implements DynamicProblem<DoubleSolution, Integer>, DoubleProblem {
  public static final int DEFAULT_TAU_T = 5;
  public static final int DEFAULT_N_T = 10;

  protected volatile double time;
  private volatile boolean changeStatus = false;

  private final int tauT;
  private final int nT;

  /** Creates the problem with tau_T = 5 and n_T = 10. */
  protected FDA() {
    this(DEFAULT_TAU_T, DEFAULT_N_T);
  }

  /**
   * Creates the problem.
   *
   * @param tauT the number of values of the counter for which t remains fixed, positive
   * @param nT the number of distinct steps in t, positive
   */
  protected FDA(int tauT, int nT) {
    Check.that(tauT > 0, () -> "tau_T must be positive (" + tauT + ")");
    Check.that(nT > 0, () -> "n_T must be positive (" + nT + ")");
    this.tauT = tauT;
    this.nT = nT;
  }

  /**
   * Sets the bounds: the first {@code numberOfVariablesInZeroOne} variables in [0, 1] and the rest
   * in [lowerBound, upperBound].
   */
  protected void setVariableBounds(
      int numberOfVariables, int numberOfVariablesInZeroOne, double lowerBound, double upperBound) {
    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Double> upperLimit = new ArrayList<>(numberOfVariables);
    for (int i = 0; i < numberOfVariables; i++) {
      boolean inZeroOne = i < numberOfVariablesInZeroOne;
      lowerLimit.add(inZeroOne ? 0.0 : lowerBound);
      upperLimit.add(inZeroOne ? 1.0 : upperBound);
    }
    variableBounds(lowerLimit, upperLimit);
  }

  /** Sets the time instant from the counter, and marks the problem as changed if it changes. */
  @Override
  public void update(Integer counter) {
    Check.notNull(counter);
    Check.that(counter >= 0, () -> "The counter must not be negative (" + counter + ")");
    double newTime = (1.0d / (double) nT) * Math.floor(counter / (double) tauT);
    if (newTime != time) {
      time = newTime;
      JMetalLogger.logger.fine(() -> "Received counter: " + counter + ". Time: " + newTime);
      setChanged();
    }
  }

  /** The current time instant t. */
  public double time() {
    return time;
  }

  public int tauT() {
    return tauT;
  }

  public int nT() {
    return nT;
  }

  @Override
  public boolean hasChanged() {
    return changeStatus;
  }

  @Override
  public void setChanged() {
    changeStatus = true;
  }

  @Override
  public void clearChanged() {
    changeStatus = false;
  }
}
