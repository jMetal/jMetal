package org.uma.jmetal.operator.selection.impl;

import java.util.List;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.util.ListUtils;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements a random selection operator used for selecting randomly a solution from a list
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class RandomSelection<S> implements SelectionOperator<List<S>, S> {

  /** Execute() method */
  public S execute(List<S> solutionList) {
    Check.notNull(solutionList);
    Check.collectionIsNotEmpty(solutionList);

    List<S> list = ListUtils.randomSelectionWithoutReplacement(1, solutionList);

    return list.get(0) ;
  }
}
