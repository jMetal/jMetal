package org.uma.jmetal.operator.selection.impl;

import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.checking.Checker;

import java.util.List;

/**
 * This class implements a random selection operator used for selecting randomly a solution from a list
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class RandomSelection<S> implements SelectionOperator<List<S>, S> {

  /** Execute() method */
  public S execute(List<S> solutionList) {
    Checker.isNotNull(solutionList);
    Checker.collectionIsNotEmpty(solutionList);

    List<S> list = SolutionListUtils.selectNRandomDifferentSolutions(1, solutionList);

    return list.get(0) ;
  }
}
