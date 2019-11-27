package org.uma.jmetal.operator.selection.impl;

import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.util.checking.Check;

import java.util.Comparator;
import java.util.List;

/**
 * This class implements a selection operator used for selecting the best solution
 * in a list according to a given comparator.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class BestSolutionSelection<S> implements SelectionOperator<List<S>, S> {
private Comparator<S> comparator ;

  public BestSolutionSelection(Comparator<S> comparator) {
    this.comparator = comparator ;
  }

  /** Execute() method */
  public S execute(List<S> solutionList) {
    Check.isNotNull(solutionList);
    Check.collectionIsNotEmpty(solutionList);

    int bestSolution = 0 ;
    for (int i = 1; i < solutionList.size(); i++) {
      if (comparator.compare(solutionList.get(i), solutionList.get(bestSolution)) < 0) {
        bestSolution = i;
      }
    }

    return solutionList.get(bestSolution) ;
  }
}
