package org.uma.jmetal.util.constrainthandling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/**
 * Stochastic ranking, in multiobjective form: the population is sorted by Runarsson and Yao's
 * stochastic bubble sort, where two adjacent solutions are compared by their Pareto rank (computed
 * ignoring the constraints) if both are feasible or, otherwise, with probability {@code pf}, and
 * by their overall constraint violation in the remaining cases. Consecutive feasible solutions of
 * the same Pareto rank form a subfront (so a density estimator can still spread them); every other
 * solution is a subfront of its own. With {@code pf = 0} feasible solutions come first, as in CDP.
 *
 * <p>A pairwise comparator mixing dominance and constraint violation at random is not transitive,
 * so the non-dominated sorting cannot use it: this technique provides its own {@link #ranking()}.
 * Its {@link #comparator()}, for pairwise decisions, is CDP.
 *
 * <p>With {@code pf > 0} infeasible solutions can be ranked before feasible ones until the end of
 * the run, so the final population of an algorithm can contain them: use an external archive,
 * which keeps the constrained-domination principle, to get the feasible solutions found.
 *
 * <p>T. P. Runarsson, X. Yao. Stochastic ranking for constrained evolutionary optimization. IEEE
 * TEVC 4(3):284-294, 2000.
 *
 * @author Nicolás Rodríguez Uribe (prototype in Evolver)
 * @param <S> the type of the solutions
 */
public class StochasticRanking<S extends Solution<?>> implements ConstraintHandlingTechnique<S> {

  private final double probabilityOfComparingObjectives;

  /**
   * Creates the technique.
   *
   * @param probabilityOfComparingObjectives the probability {@code pf}, in [0, 1], of comparing two
   *     solutions, one of them at least infeasible, by their Pareto rank
   */
  public StochasticRanking(double probabilityOfComparingObjectives) {
    Check.probabilityIsValid(probabilityOfComparingObjectives);
    this.probabilityOfComparingObjectives = probabilityOfComparingObjectives;
  }

  @Override
  public Comparator<S> comparator() {
    return new DominanceWithConstraintsComparator<>();
  }

  @Override
  public Ranking<S> ranking() {
    return new StochasticRankingSort();
  }

  /** The stochastic bubble sort, as a ranking whose subfronts follow the sorted order. */
  private final class StochasticRankingSort implements Ranking<S> {
    private final String attributeId = StochasticRanking.class.getName();
    private List<List<S>> subFronts = new ArrayList<>();

    @Override
    public Ranking<S> compute(List<S> solutionList) {
      Map<S, Integer> paretoRank = paretoRanksIgnoringConstraints(solutionList);
      List<S> sorted = new ArrayList<>(solutionList);
      for (int sweep = 0; sweep < sorted.size(); sweep++) {
        boolean swapped = false;
        for (int j = 0; j < sorted.size() - 1; j++) {
          S first = sorted.get(j);
          S second = sorted.get(j + 1);
          boolean byRank =
              (ConstraintHandling.isFeasible(first) && ConstraintHandling.isFeasible(second))
                  || JMetalRandom.getInstance().nextDouble() < probabilityOfComparingObjectives;
          boolean secondGoesFirst =
              byRank
                  ? paretoRank.get(second) < paretoRank.get(first)
                  : violation(second) < violation(first);
          if (secondGoesFirst) {
            sorted.set(j, second);
            sorted.set(j + 1, first);
            swapped = true;
          }
        }
        if (!swapped) {
          break;
        }
      }
      subFronts = groupIntoSubFronts(sorted, paretoRank);
      return this;
    }

    private Map<S, Integer> paretoRanksIgnoringConstraints(List<S> solutionList) {
      Map<S, Integer> ranks = new IdentityHashMap<>();
      var ranking = new FastNonDominatedSortRanking<S>(new DefaultDominanceComparator<>());
      ranking.compute(solutionList);
      for (S solution : solutionList) {
        ranks.put(solution, ranking.getRank(solution));
      }
      return ranks;
    }

    private List<List<S>> groupIntoSubFronts(List<S> sorted, Map<S, Integer> paretoRank) {
      List<List<S>> groups = new ArrayList<>();
      S previous = null;
      for (S solution : sorted) {
        boolean sameSubFront =
            previous != null
                && ConstraintHandling.isFeasible(previous)
                && ConstraintHandling.isFeasible(solution)
                && paretoRank.get(previous).equals(paretoRank.get(solution));
        if (!sameSubFront) {
          groups.add(new ArrayList<>());
        }
        groups.get(groups.size() - 1).add(solution);
        solution.attributes().put(attributeId, groups.size() - 1);
        previous = solution;
      }
      return groups;
    }

    @Override
    public List<S> getSubFront(int rank) {
      Check.that(rank < subFronts.size(), "Invalid rank: " + rank);
      return subFronts.get(rank);
    }

    @Override
    public int getNumberOfSubFronts() {
      return subFronts.size();
    }

    @Override
    public Integer getRank(S solution) {
      Object rank = solution.attributes().get(attributeId);
      return rank == null ? -1 : (Integer) rank;
    }

    @Override
    public Object getAttributedId() {
      return attributeId;
    }
  }

  private static <S extends Solution<?>> double violation(S solution) {
    return Math.abs(ConstraintHandling.overallConstraintViolationDegree(solution));
  }
}
