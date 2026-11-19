package org.uma.jmetal.algorithm.dynamic;

import java.util.List;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.restartstrategy.RestartStrategy;

/**
 * A metaheuristic that a {@link DynamicAlgorithm} runs one iteration at a time, so that it can
 * react to changes between iterations. Each method returns the number of evaluations it made, so
 * that the dynamic algorithm counts all of them.
 *
 * @param <S> the type of the solutions
 */
public interface StepwiseAlgorithm<S extends Solution<?>> {
  /** Creates and evaluates the initial population, and returns the evaluations made. */
  int initialize();

  /** Runs one iteration (a generation, or a move of the swarm), and returns the evaluations made. */
  int step();

  /**
   * Restarts the search after a change: applies the strategy to the population, evaluates the whole
   * population again (the objectives of the solutions that remain are no longer valid after a change
   * of the problem) and resets the state that depends on the old objectives (archives, leaders,
   * memories, ideal and nadir points). Returns the evaluations made.
   *
   * @param restartStrategy the strategy that removes solutions and creates new ones
   * @param problem the problem, whose current definition evaluates the population
   */
  int restart(RestartStrategy<S> restartStrategy, DynamicProblem<S, ?> problem);

  /** The current population. */
  List<S> population();

  /** The current approximation of the front. */
  List<S> result();

  String name();
}
