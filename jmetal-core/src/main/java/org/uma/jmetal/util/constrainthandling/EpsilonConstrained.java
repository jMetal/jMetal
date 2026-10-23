package org.uma.jmetal.util.constrainthandling;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * The epsilon-constrained method: a solution whose overall constraint violation is at most epsilon
 * counts as feasible, and solutions are then compared as in the constrained-domination principle.
 * Epsilon starts at the violation of the solution at a given quantile of the first population
 * ranked and decreases to zero, so the search first uses infeasible solutions close to the feasible
 * region and ends as the constrained-domination principle:
 *
 * <pre>
 *   epsilon(t) = epsilon(0) * (1 - t / Tc)^exponent   if t &lt; Tc,   0 otherwise
 * </pre>
 *
 * where t is the progress of the run, in [0, 1], and Tc the fraction of the run from which epsilon
 * is zero. The three parameters are scale-free: a quantile, an exponent and a fraction of the run,
 * so a setting can be used on problems whose constraints have different scales. The progress comes
 * from the termination condition, so it needs a termination by evaluations or by computing time.
 * Before the first update epsilon is zero.
 *
 * <p>T. Takahama, S. Sakai. Constrained optimization by the epsilon constrained differential
 * evolution with gradient-based mutation and feasible elites. IEEE CEC 2006, 1-8.
 *
 * @author Nicolás Rodríguez Uribe (prototype in Evolver)
 * @param <S> the type of the solutions
 */
public class EpsilonConstrained<S extends Solution<?>>
    implements PairwiseConstraintHandlingTechnique<S> {

  private final double initialQuantile;
  private final double exponent;
  private final double decayFraction;

  private boolean started;
  private double initialEpsilon;
  private double epsilon;

  /**
   * Creates the technique.
   *
   * @param initialQuantile the quantile, in [0, 1], of the violations of the first population that
   *     gives the initial epsilon
   * @param exponent the exponent of the decrease of epsilon (positive)
   * @param decayFraction the fraction of the run, in (0, 1], from which epsilon is zero (Tc)
   */
  public EpsilonConstrained(double initialQuantile, double exponent, double decayFraction) {
    Check.probabilityIsValid(initialQuantile);
    Check.that(exponent > 0, "The exponent must be positive: " + exponent);
    Check.that(
        decayFraction > 0 && decayFraction <= 1,
        "The decay fraction must be in (0, 1]: " + decayFraction);
    this.initialQuantile = initialQuantile;
    this.exponent = exponent;
    this.decayFraction = decayFraction;
  }

  @Override
  public void update(List<S> population, double progress) {
    checkProgress(progress, "EpsilonConstrained");
    if (!started) {
      initialEpsilon = violationAtQuantile(population);
      started = true;
    }
    epsilon =
        progress < decayFraction
            ? initialEpsilon * Math.pow(1.0 - progress / decayFraction, exponent)
            : 0.0;
  }

  /** The current epsilon. */
  public double epsilon() {
    return epsilon;
  }

  @Override
  public int compare(S solution1, S solution2, Comparator<S> objectiveComparison) {
    return compareWithEpsilon(solution1, solution2, epsilon, objectiveComparison);
  }

  /**
   * Compares two solutions with the epsilon-constrained method: solutions whose violation is at
   * most epsilon count as feasible, and they are then compared as in the constrained-domination
   * principle.
   */
  static <S extends Solution<?>> int compareWithEpsilon(
      S solution1, S solution2, double epsilon, Comparator<S> objectiveComparison) {
    double violation1 = violation(solution1);
    double violation2 = violation(solution2);
    boolean feasible1 = violation1 <= epsilon;
    boolean feasible2 = violation2 <= epsilon;
    if (feasible1 && feasible2) {
      return objectiveComparison.compare(solution1, solution2);
    }
    if (feasible1) {
      return -1;
    }
    if (feasible2) {
      return 1;
    }
    return Double.compare(violation1, violation2);
  }

  private double violationAtQuantile(List<S> population) {
    Check.that(!population.isEmpty(), "The population is empty");
    double[] violations = population.stream().mapToDouble(EpsilonConstrained::violation).toArray();
    Arrays.sort(violations);
    return violations[(int) Math.floor(initialQuantile * (violations.length - 1))];
  }

  /** The overall constraint violation as a non-negative value (jMetal stores it as negative). */
  static <S extends Solution<?>> double violation(S solution) {
    return Math.abs(ConstraintHandling.overallConstraintViolationDegree(solution));
  }

  /** Checks that a technique that changes during the run is told the progress of the run. */
  static void checkProgress(double progress, String technique) {
    Check.that(
        !Double.isNaN(progress),
        technique
            + " changes during the run and needs its progress: use a termination by evaluations"
            + " or by computing time");
    Check.that(
        progress >= 0.0 && progress <= 1.0, "The progress must be in [0, 1]: " + progress);
  }
}
