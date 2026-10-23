package org.uma.jmetal.util.constrainthandling;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Push and pull search (PPS). The search first ignores the constraints (push stage), so that the
 * population crosses the infeasible regions towards the unconstrained Pareto front, and then pulls
 * it to the feasible region with an improved epsilon-constrained method (pull stage).
 *
 * <p>The push stage ends when the ideal and nadir points of the population settle: when the largest
 * relative change of their coordinates over the last part of the run of length l is at most a
 * threshold. Epsilon then starts at the largest overall constraint violation of the population and,
 * at each update, becomes
 *
 * <pre>
 *   epsilon(t) = (1 - tau) * epsilon               if rf &lt; alpha
 *   epsilon(t) = epsilon(0) * (1 - t / Tc)^cp       otherwise
 * </pre>
 *
 * where t is the progress of the run, in [0, 1], and rf the ratio of feasible solutions of the
 * population; solutions within epsilon are compared as in {@link EpsilonConstrained}. From Tc on
 * epsilon is zero, and the push stage also ends there if the points have not settled before, so
 * that a run always ends as the constrained-domination principle (in the original method, a push
 * stage that never settles ignores the constraints to the end).
 *
 * <p>The ideal and nadir points are those of the solutions of the population that are not dominated
 * in it, ignoring the constraints. The window l and Tc are fractions of the run, so that the
 * parameters are scale-free. The progress comes from the termination condition, so it needs a
 * termination by evaluations or by computing time. The reduction by tau is applied once per update,
 * which the algorithms make once per generation.
 *
 * <p>Z. Fan, W. Li, X. Cai, H. Li, C. Wei, Q. Zhang, K. Deb, E. Goodman. Push and pull search for
 * solving constrained multi-objective optimization problems. Swarm and Evolutionary Computation
 * 44:665-679, 2019.
 *
 * @author Nicolás Rodríguez Uribe (prototype in Evolver)
 * @param <S> the type of the solutions
 */
public class PushPullSearch<S extends Solution<?>>
    implements PairwiseConstraintHandlingTechnique<S> {

  /** The smallest denominator of a relative change, for coordinates close to zero. */
  private static final double DELTA = 1e-6;

  /** The tolerance of the comparisons of progress, whose values come from divisions. */
  private static final double PROGRESS_TOLERANCE = 1e-12;

  /** The ideal and nadir points of a population, and the progress of the run when it was seen. */
  private record Points(double progress, double[] ideal, double[] nadir) {}

  private final double changeThreshold;
  private final double windowFraction;
  private final double decayFraction;
  private final double exponent;
  private final double feasibilityThreshold;
  private final double reductionRate;
  private final Comparator<S> dominance = new DefaultDominanceComparator<>();
  private final Deque<Points> history = new ArrayDeque<>();

  private boolean pushing = true;
  private double initialEpsilon;
  private double epsilon;

  /**
   * Creates the technique.
   *
   * @param changeThreshold the largest relative change of the ideal and nadir points over the
   *     window that ends the push stage (non-negative)
   * @param windowFraction the window l, in (0, 1], as a fraction of the run
   * @param decayFraction the fraction of the run, in (0, 1], from which epsilon is zero (Tc)
   * @param exponent the exponent cp of the decrease of epsilon (positive)
   * @param feasibilityThreshold the ratio alpha of feasible solutions, in [0, 1], from which
   *     epsilon follows its schedule instead of being reduced
   * @param reductionRate the reduction tau of epsilon, in [0, 1], while the ratio of feasible
   *     solutions is below alpha
   */
  public PushPullSearch(
      double changeThreshold,
      double windowFraction,
      double decayFraction,
      double exponent,
      double feasibilityThreshold,
      double reductionRate) {
    Check.that(
        changeThreshold >= 0, "The change threshold must be non-negative: " + changeThreshold);
    Check.that(
        windowFraction > 0 && windowFraction <= 1,
        "The window fraction must be in (0, 1]: " + windowFraction);
    Check.that(
        decayFraction > 0 && decayFraction <= 1,
        "The decay fraction must be in (0, 1]: " + decayFraction);
    Check.that(exponent > 0, "The exponent must be positive: " + exponent);
    Check.probabilityIsValid(feasibilityThreshold);
    Check.probabilityIsValid(reductionRate);
    this.changeThreshold = changeThreshold;
    this.windowFraction = windowFraction;
    this.decayFraction = decayFraction;
    this.exponent = exponent;
    this.feasibilityThreshold = feasibilityThreshold;
    this.reductionRate = reductionRate;
  }

  @Override
  public void update(List<S> population, double progress) {
    EpsilonConstrained.checkProgress(progress, "PushPullSearch");
    Check.that(!population.isEmpty(), "The population is empty");
    if (progress >= decayFraction) {
      pushing = false;
      epsilon = 0.0;
      return;
    }
    if (pushing) {
      history.addLast(idealAndNadirPoints(population, progress));
      if (!settled()) {
        return;
      }
      pushing = false;
      history.clear();
      initialEpsilon =
          population.stream().mapToDouble(EpsilonConstrained::violation).max().orElse(0.0);
      epsilon = initialEpsilon;
    }
    epsilon =
        ConstraintHandling.feasibilityRatio(population) < feasibilityThreshold
            ? (1.0 - reductionRate) * epsilon
            : initialEpsilon * Math.pow(1.0 - progress / decayFraction, exponent);
  }

  /** Whether the search is in the push stage, which ignores the constraints. */
  public boolean pushing() {
    return pushing;
  }

  /** The current epsilon of the pull stage. */
  public double epsilon() {
    return epsilon;
  }

  @Override
  public int compare(S solution1, S solution2, Comparator<S> objectiveComparison) {
    return pushing
        ? objectiveComparison.compare(solution1, solution2)
        : EpsilonConstrained.compareWithEpsilon(solution1, solution2, epsilon, objectiveComparison);
  }

  private Points idealAndNadirPoints(List<S> population, double progress) {
    int numberOfObjectives = population.get(0).objectives().length;
    double[] ideal = new double[numberOfObjectives];
    double[] nadir = new double[numberOfObjectives];
    Arrays.fill(ideal, Double.POSITIVE_INFINITY);
    Arrays.fill(nadir, Double.NEGATIVE_INFINITY);
    for (S solution : population) {
      boolean dominated = false;
      for (S other : population) {
        if (dominance.compare(other, solution) < 0) {
          dominated = true;
          break;
        }
      }
      for (int i = 0; i < numberOfObjectives; i++) {
        ideal[i] = Math.min(ideal[i], solution.objectives()[i]);
        if (!dominated) {
          nadir[i] = Math.max(nadir[i], solution.objectives()[i]);
        }
      }
    }
    return new Points(progress, ideal, nadir);
  }

  /**
   * Whether the ideal and nadir points changed at most the threshold since the last points seen at
   * least a window before the current ones. The older points are dropped.
   */
  private boolean settled() {
    Points current = history.getLast();
    double start = current.progress() - windowFraction + PROGRESS_TOLERANCE;
    if (history.getFirst().progress() > start) {
      return false;
    }
    while (history.size() > 2 && getSecond().progress() <= start) {
      history.removeFirst();
    }
    Points reference = history.getFirst();
    return Math.max(
            largestRelativeChange(reference.ideal(), current.ideal()),
            largestRelativeChange(reference.nadir(), current.nadir()))
        <= changeThreshold;
  }

  private Points getSecond() {
    var iterator = history.iterator();
    iterator.next();
    return iterator.next();
  }

  private static double largestRelativeChange(double[] previous, double[] current) {
    double change = 0.0;
    for (int i = 0; i < previous.length; i++) {
      change =
          Math.max(
              change, Math.abs(current[i] - previous[i]) / Math.max(Math.abs(previous[i]), DELTA));
    }
    return change;
  }
}
