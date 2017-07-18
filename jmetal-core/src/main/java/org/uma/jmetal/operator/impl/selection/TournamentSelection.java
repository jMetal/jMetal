package org.uma.jmetal.operator.impl.selection;

import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.SolutionUtils;
import org.uma.jmetal.util.comparator.DominanceComparator;

import java.util.Comparator;
import java.util.List;

/**
 * @author Juanjo
 * @version 1.0
 *
 * Applies a n-ary tournament selection to return a solution from a list.
 */
@SuppressWarnings("serial")
public class TournamentSelection<S extends Solution<?>> implements SelectionOperator<List<S>,S> {
  private Comparator<S> comparator;

  private final int numberOfTournaments;

  /** Constructor */
  public TournamentSelection(int numberOfTournaments) {
    this(new DominanceComparator<S>(), numberOfTournaments) ;
  }

  /** Constructor */
  public TournamentSelection(Comparator<S> comparator, int numberOfTournaments) {
    this.numberOfTournaments = numberOfTournaments;
    this.comparator = comparator ;
  }

  @Override
  /** Execute() method */
  public S execute(List<S> solutionList) {
    if (null == solutionList) {
      throw new JMetalException("The solution list is null") ;
    } else if (solutionList.isEmpty()) {
      throw new JMetalException("The solution list is empty") ;
    }

    S result;
    if (solutionList.size() == 1) {
      result = solutionList.get(0);
    } else {
      result = SolutionListUtils.selectNRandomDifferentSolutions(1, solutionList).get(0);
      int count = 1; // at least 2 solutions are compared
      do {
        S candidate = SolutionListUtils.selectNRandomDifferentSolutions(1, solutionList).get(0);
        result = SolutionUtils.getBestSolution(result, candidate, comparator) ;
      } while (++count < this.numberOfTournaments);
    }

    return result;
  }
}
