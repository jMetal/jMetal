package org.uma.jmetal.util.constrainthandling;

import java.util.Comparator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/**
 * A constraint-handling technique that decides between two solutions by looking at their
 * constraints and, when the constraints do not decide, by a comparison of their objectives that
 * the algorithm provides: Pareto dominance in the algorithms based on dominance (the default
 * {@link #comparator()}), or the value of the aggregation function of a subproblem in MOEA/D.
 * That makes these techniques usable by any algorithm.
 *
 * @param <S> the type of the solutions
 */
public interface PairwiseConstraintHandlingTechnique<S extends Solution<?>>
    extends ConstraintHandlingTechnique<S> {

  /**
   * Compares two solutions with this technique.
   *
   * @param solution1 the first solution
   * @param solution2 the second solution
   * @param objectiveComparison the comparison of the objectives of the two solutions, used when
   *     the constraints do not decide
   * @return a negative value if the first solution is preferred, a positive one if the second one
   *     is, and zero if neither
   */
  int compare(S solution1, S solution2, Comparator<S> objectiveComparison);

  /** The comparison of this technique with Pareto dominance as the comparison of the objectives. */
  @Override
  default Comparator<S> comparator() {
    Comparator<S> dominance = new DefaultDominanceComparator<>();
    return (solution1, solution2) -> compare(solution1, solution2, dominance);
  }

  /** The fast non-dominated sorting with {@link #comparator()}. */
  @Override
  default Ranking<S> ranking() {
    return new FastNonDominatedSortRanking<>(comparator());
  }
}
