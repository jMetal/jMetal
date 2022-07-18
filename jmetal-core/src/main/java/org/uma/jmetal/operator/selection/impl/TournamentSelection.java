package org.uma.jmetal.operator.selection.impl;

import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.SolutionUtils;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * @author Juanjo
 * @version 1.0
 *
 * Applies a n-ary tournament selection to return a solution from a list.
 */
@SuppressWarnings("serial")
@Deprecated
public class TournamentSelection<S extends Solution<?>> implements SelectionOperator<List<S>,S> {
  private Comparator<S> comparator;

  private final int n_arity;

  /** Constructor */
  public TournamentSelection(int n_arity) {
    this(new DominanceWithConstraintsComparator<S>(), n_arity) ;
  }

  /** Constructor */
  public TournamentSelection(Comparator<S> comparator, int n_arity) {
    this.n_arity = n_arity;
    this.comparator = comparator ;
  }

  @Override
  /** Execute() method */
  public S execute(List<S> solutionList) {
    Check.notNull(solutionList);
    Check.collectionIsNotEmpty(solutionList);

    S result;
    if (solutionList.size() == 1) {
      result = solutionList.get(0);
    } else {
      result = SolutionListUtils.selectNRandomDifferentSolutions(1, solutionList).get(0);
      var count = 1; // at least 2 solutions are compared
      do {
        var candidate = SolutionListUtils.selectNRandomDifferentSolutions(1, solutionList).get(0);
        result = SolutionUtils.getBestSolution(result, candidate, comparator) ;
      } while (++count < this.n_arity);
    }

    return result;
  }
}
