package org.uma.jmetal.operator.impl.selection;

import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.util.JMetalException;

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
    if (null == solutionList) {
      throw new JMetalException("The solution list is null") ;
    } else if (solutionList.isEmpty()) {
      throw new JMetalException("The solution list is empty") ;
    }

    int bestSolution = 0 ;
    for (int i = 1; i < solutionList.size(); i++) {
      if (comparator.compare(solutionList.get(i), solutionList.get(bestSolution)) < 0) {
        bestSolution = i;
      }
    }

    return solutionList.get(bestSolution) ;
  }
}
