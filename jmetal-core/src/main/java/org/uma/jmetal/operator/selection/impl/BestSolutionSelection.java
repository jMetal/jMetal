package org.uma.jmetal.operator.selection.impl;

import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements a selection operator used for selecting the best solution
 * in a list according to a given comparator.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class BestSolutionSelection<S> implements SelectionOperator<List<S>, S> {
private final @NotNull Comparator<S> comparator ;

  public BestSolutionSelection(@NotNull Comparator<S> comparator) {
    Check.notNull(comparator);
    this.comparator = comparator ;
  }

  /** Execute() method */
  public S execute(List<S> solutionList) {
    Check.notNull(solutionList);
    Check.collectionIsNotEmpty(solutionList);

    S acc = solutionList.get(0);
    for (S s : solutionList) {
      acc = (comparator.compare(acc, s) < 0) ? acc : s;
    }
    return acc;
  }
}
