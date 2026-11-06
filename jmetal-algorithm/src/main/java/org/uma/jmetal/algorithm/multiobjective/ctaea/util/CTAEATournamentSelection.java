package org.uma.jmetal.algorithm.multiobjective.ctaea.util;

import java.util.List;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Binary tournament of C-TAEA, transcribed from the {@code MatingSelection} function of PlatEMO.
 * Two solutions are drawn at random (with replacement) from an archive:
 *
 * <ul>
 *   <li>if only one of them is feasible, it wins;
 *   <li>if both are feasible, the first one wins unless the second one dominates it (Pareto
 *       dominance on the objectives);
 *   <li>if both are infeasible, the winner is a third solution drawn at random from the archive.
 * </ul>
 *
 * <p>In Algorithm 5 of the paper, the winner between two infeasible solutions, or between two
 * feasible non-dominated ones, is one of the two at random. As both solutions are drawn uniformly
 * from the archive, the winner has the same distribution in PlatEMO and in the paper.
 *
 * @param <S> the type of the solutions
 */
public class CTAEATournamentSelection<S extends Solution<?>>
    implements SelectionOperator<List<S>, S> {

  @Override
  public S execute(List<S> archive) {
    Check.collectionIsNotEmpty(archive, "archive");

    JMetalRandom random = JMetalRandom.getInstance();
    int last = archive.size() - 1;
    S first = archive.get(random.nextInt(0, last));
    S second = archive.get(random.nextInt(0, last));
    boolean firstIsFeasible = ConstraintViolation.isFeasible(first);
    boolean secondIsFeasible = ConstraintViolation.isFeasible(second);

    S winner;
    if (!firstIsFeasible && !secondIsFeasible) {
      winner = archive.get(random.nextInt(0, last));
    } else if (firstIsFeasible && !secondIsFeasible) {
      winner = first;
    } else if (!firstIsFeasible) {
      winner = second;
    } else {
      winner =
          NonDominatedSorting.dominates(second.objectives(), first.objectives()) ? second : first;
    }
    return winner;
  }
}
