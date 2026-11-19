package org.uma.jmetal.algorithm.dynamic;

import org.uma.jmetal.util.errorchecking.Check;

/**
 * Condition that ends the run of a {@link DynamicAlgorithm}. It is checked before each iteration,
 * and the run also ends when {@link DynamicAlgorithm#requestStop()} is called.
 *
 * <p>{@link #afterEvaluations(long)} counts all the evaluations, so the re-evaluations of the
 * population after each change take part of the budget. {@link #afterIterations(long)} counts
 * generations, as the protocol of the CEC 2018 competition on dynamic multiobjective optimisation
 * (whose budget of 100 (30 tau_t + 50) evaluations is 30 tau_t + 50 generations of 100 solutions),
 * and {@link #afterProblemChanges(int)} reproduces its 30 changes per run.
 */
@FunctionalInterface
public interface DynamicStoppingCondition {
  boolean isMet(DynamicStatus status);

  /** The run never ends by itself; only {@link DynamicAlgorithm#requestStop()} ends it. */
  static DynamicStoppingCondition never() {
    return status -> false;
  }

  /** Ends the run after the given number of iterations. */
  static DynamicStoppingCondition afterIterations(long iterations) {
    Check.that(iterations > 0, () -> "The number of iterations must be positive: " + iterations);
    return status -> status.iterations() >= iterations;
  }

  /** Ends the run after the given number of evaluations, re-evaluations included. */
  static DynamicStoppingCondition afterEvaluations(long evaluations) {
    Check.that(evaluations > 0, () -> "The number of evaluations must be positive: " + evaluations);
    return status -> status.evaluations() >= evaluations;
  }

  /** Ends the run after the given number of rounds. */
  static DynamicStoppingCondition afterRounds(int rounds) {
    Check.that(rounds > 0, () -> "The number of rounds must be positive: " + rounds);
    return status -> status.rounds() >= rounds;
  }

  /** Ends the run after the algorithm has reacted to the given number of changes of the problem. */
  static DynamicStoppingCondition afterProblemChanges(int changes) {
    Check.that(changes > 0, () -> "The number of changes must be positive: " + changes);
    return status -> status.problemChanges() >= changes;
  }

  /** Ends the run after the given computing time, in milliseconds. */
  static DynamicStoppingCondition afterComputingTime(long milliseconds) {
    Check.that(milliseconds > 0, () -> "The computing time must be positive: " + milliseconds);
    return status -> status.computingTime() >= milliseconds;
  }

  /** A condition that is met when this one or the other one is met. */
  default DynamicStoppingCondition or(DynamicStoppingCondition other) {
    Check.notNull(other);
    return status -> isMet(status) || other.isMet(status);
  }
}
