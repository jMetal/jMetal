package org.uma.jmetal.operator.selection.impl;

import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Applies a N-ary tournament selection to return the best solution between N that have been chosen
 * at random from a solution list.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class NaryTournamentSelection<S extends Solution<?>>
    implements SelectionOperator<List<S>, S> {
  private Comparator<S> comparator;
  private int tournamentSize;

  /** Constructor */
  public NaryTournamentSelection() {
    this(2, new DominanceWithConstraintsComparator<S>());
  }

  /** Constructor */
  public NaryTournamentSelection(int tournamentSize, Comparator<S> comparator) {
    this.tournamentSize = tournamentSize;
    this.comparator = comparator;
  }

  @Override
  /** Execute() method */
  public S execute(@NotNull List<S> solutionList) {
    Check.notNull(solutionList);
    Check.collectionIsNotEmpty(solutionList);
    Check.that(
        solutionList.size() >= tournamentSize,
        "The solution list size ("
            + solutionList.size()
            + ") is less than "
            + "the number of requested solutions ("
            + tournamentSize
            + ")");

    S result;
    if (solutionList.size() == 1) {
      result = solutionList.get(0);
    } else {
      @NotNull List<S> selectedSolutions =
          SolutionListUtils.selectNRandomDifferentSolutions(
                  tournamentSize, solutionList);
      result = SolutionListUtils.findBestSolution(selectedSolutions, comparator);
    }

    return result;
  }

  public int getTournamentSize() {
    return tournamentSize ;
  }
}
