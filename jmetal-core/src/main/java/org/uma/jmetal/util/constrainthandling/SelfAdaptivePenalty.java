package org.uma.jmetal.util.constrainthandling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

/**
 * The self-adaptive penalty function, in its multiobjective form: the population is sorted into
 * fronts by Pareto dominance on modified objectives, which add to each objective a distance and two
 * penalties weighted by the ratio rf of feasible solutions of the population. For a solution x, the
 * modified i-th objective is
 *
 * <pre>
 *   F_i(x) = d_i(x) + (1 - rf) * X(x) + rf * Y_i(x)
 *   d_i(x) = v(x) if rf = 0,   sqrt(f_i(x)^2 + v(x)^2) otherwise
 *   X(x)   = 0    if rf = 0,   v(x) otherwise
 *   Y_i(x) = 0    if x is feasible,   f_i(x) otherwise
 * </pre>
 *
 * where f_i is the i-th objective normalized to [0, 1] with its minimum and maximum in the
 * population, and v(x) the mean over the constraints of the violation of each one divided by its
 * largest violation in the population. With no feasible solutions only the violation counts; as
 * the ratio of feasible solutions grows, infeasible solutions with good objectives can still rank
 * well. It has no parameters; with no constraints it is Pareto dominance.
 *
 * <p>On a problem whose feasible region is very narrow, the population can stay just outside it:
 * a solution with a tiny violation and better objectives outranks a feasible one when few are
 * feasible. On LIR-CMOP1 to LIR-CMOP4, NSGA-II with this technique ends with violations of 1e-4 to
 * 1e-7 and hardly any feasible solution in its population, although it finds feasible solutions
 * along the run: an external archive, which keeps the constrained-domination principle, keeps
 * them.
 *
 * <p>The modified objectives depend on the whole population, so this technique provides its own
 * {@link #ranking()}. Its {@link #comparator()}, for pairwise decisions, is CDP.
 *
 * <p>Y. G. Woldesenbet, G. G. Yen, B. G. Tessema. Constraint handling in multiobjective
 * evolutionary optimization. IEEE TEVC 13(3):514-525, 2009.
 *
 * @author Nicolás Rodríguez Uribe (prototype in Evolver)
 * @param <S> the type of the solutions
 */
public class SelfAdaptivePenalty<S extends Solution<?>> implements ConstraintHandlingTechnique<S> {

  @Override
  public Comparator<S> comparator() {
    return new DominanceWithConstraintsComparator<>();
  }

  @Override
  public Ranking<S> ranking() {
    return new SelfAdaptivePenaltyRanking();
  }

  /**
   * The modified objectives of the solutions of a population.
   *
   * @param population the population
   * @param <S> the type of the solutions
   * @return the modified objectives of each solution
   */
  static <S extends Solution<?>> Map<S, double[]> modifiedObjectives(List<S> population) {
    int numberOfObjectives = population.get(0).objectives().length;
    int numberOfConstraints = population.get(0).constraints().length;
    double[] minimum = new double[numberOfObjectives];
    double[] maximum = new double[numberOfObjectives];
    Arrays.fill(minimum, Double.POSITIVE_INFINITY);
    Arrays.fill(maximum, Double.NEGATIVE_INFINITY);
    double[] largestViolation = new double[numberOfConstraints];
    int feasible = 0;
    for (S solution : population) {
      for (int i = 0; i < numberOfObjectives; i++) {
        minimum[i] = Math.min(minimum[i], solution.objectives()[i]);
        maximum[i] = Math.max(maximum[i], solution.objectives()[i]);
      }
      for (int j = 0; j < numberOfConstraints; j++) {
        largestViolation[j] = Math.max(largestViolation[j], -solution.constraints()[j]);
      }
      if (ConstraintHandling.isFeasible(solution)) {
        feasible++;
      }
    }
    double feasibleRatio = (double) feasible / population.size();

    Map<S, double[]> modified = new IdentityHashMap<>();
    for (S solution : population) {
      double violation = 0.0;
      for (int j = 0; j < numberOfConstraints; j++) {
        if (largestViolation[j] > 0) {
          violation += Math.max(0.0, -solution.constraints()[j]) / largestViolation[j];
        }
      }
      violation = numberOfConstraints > 0 ? violation / numberOfConstraints : 0.0;
      boolean isFeasible = ConstraintHandling.isFeasible(solution);
      double[] objectives = new double[numberOfObjectives];
      for (int i = 0; i < numberOfObjectives; i++) {
        double range = maximum[i] - minimum[i];
        double normalized = range > 0 ? (solution.objectives()[i] - minimum[i]) / range : 0.0;
        double distance =
            feasibleRatio == 0
                ? violation
                : Math.sqrt(normalized * normalized + violation * violation);
        double violationPenalty = feasibleRatio == 0 ? 0.0 : violation;
        double objectivePenalty = isFeasible ? 0.0 : normalized;
        objectives[i] =
            distance + (1.0 - feasibleRatio) * violationPenalty + feasibleRatio * objectivePenalty;
      }
      modified.put(solution, objectives);
    }
    return modified;
  }

  /** The non-dominated sorting on the modified objectives of each population ranked. */
  private final class SelfAdaptivePenaltyRanking implements Ranking<S> {
    private final String attributeId = SelfAdaptivePenalty.class.getName();
    private List<List<S>> subFronts = new ArrayList<>();

    @Override
    public Ranking<S> compute(List<S> solutionList) {
      subFronts = new ArrayList<>();
      if (solutionList.isEmpty()) {
        return this;
      }
      Map<S, double[]> modified = modifiedObjectives(solutionList);
      var sorting =
          new FastNonDominatedSortRanking<S>(
              (solution1, solution2) ->
                  VectorUtils.dominanceTest(modified.get(solution1), modified.get(solution2)));
      sorting.compute(solutionList);
      for (int rank = 0; rank < sorting.getNumberOfSubFronts(); rank++) {
        List<S> subFront = new ArrayList<>(sorting.getSubFront(rank));
        for (S solution : subFront) {
          solution.attributes().put(attributeId, rank);
        }
        subFronts.add(subFront);
      }
      return this;
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
}
