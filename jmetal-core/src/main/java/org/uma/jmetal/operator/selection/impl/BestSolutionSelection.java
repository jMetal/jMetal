package org.uma.jmetal.operator.selection.impl;

import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements a selection operator used for selecting the best solution
 * in a list according to a given comparator.
 *
 * @author Antonio J. Nebro
 */
public class BestSolutionSelection<S> implements SelectionOperator<List<S>, S> {
private final Comparator<S> comparator ;

  public BestSolutionSelection(Comparator<S> comparator) {
    Check.notNull(comparator);
    this.comparator = comparator ;
  }

  /** Execute() method */
  public S execute(List<S> solutionList) {
    Check.notNull(solutionList);
    Check.collectionIsNotEmpty(solutionList);

    return solutionList.stream().reduce(solutionList.get(0), (x, y) -> (comparator.compare(x, y) < 0) ? x : y);
  }
}
