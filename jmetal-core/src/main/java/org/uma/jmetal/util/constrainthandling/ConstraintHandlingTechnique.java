package org.uma.jmetal.util.constrainthandling;

import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ranking.Ranking;

/**
 * A constraint-handling technique (CHT): how an algorithm decides between solutions of a problem
 * with constraints, so that the same algorithm can handle them in different ways by taking a
 * different technique. A constraint value lower than zero is a violation (jMetal's convention).
 *
 * <p>There are two kinds of techniques:
 *
 * <ul>
 *   <li>{@link PairwiseConstraintHandlingTechnique}: a rule that looks at the constraints of two
 *       solutions and, when they do not decide, delegates in a comparison of their objectives
 *       (Pareto dominance, the value of an aggregation function...). They can be used by any
 *       algorithm: {@link ConstrainedDominance}, {@link EpsilonConstrained} and {@link
 *       PushPullSearch}.
 *   <li>Techniques that need the whole population to rank it, such as {@link StochasticRanking} and
 *       {@link SelfAdaptivePenalty}. They can only be used by algorithms that rank their population
 *       ({@link #ranking()}); their {@link #comparator()}, for the pairwise decisions those
 *       algorithms may also take, is Deb's constrained-domination principle.
 * </ul>
 *
 * <p>The techniques whose behaviour changes during the run are told about each generation through
 * {@link #update(List, double)}, which receives the progress of the run. They have state, so an
 * instance must not be shared by several runs.
 *
 * <p>A technique only guides the search: the archives that keep the result of an algorithm
 * compare solutions with the constrained-domination principle, whatever the technique.
 *
 * @param <S> the type of the solutions
 */
public interface ConstraintHandlingTechnique<S extends Solution<?>> {

  /**
   * A new ranking that sorts a population with this technique. An algorithm should call it once
   * and use the same ranking in its selection and its replacement.
   *
   * @return the ranking
   */
  Ranking<S> ranking();

  /**
   * The comparator of this technique for pairwise decisions, with the contract of a dominance
   * comparator: a negative value if the first solution is preferred, a positive one if the second
   * one is, and zero if neither.
   *
   * @return the comparator
   */
  Comparator<S> comparator();

  /**
   * Updates the state of the technique once per generation (or the equivalent number of
   * evaluations in a steady-state algorithm), before the population is ranked. Techniques that do
   * not change during the run ignore it.
   *
   * @param population the solutions about to be ranked
   * @param progress the fraction of the run that has been done, in [0, 1], or {@code NaN} if the
   *     termination condition of the algorithm does not tell it
   */
  default void update(List<S> population, double progress) {}
}
