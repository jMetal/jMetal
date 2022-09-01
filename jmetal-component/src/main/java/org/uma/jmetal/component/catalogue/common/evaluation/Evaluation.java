package org.uma.jmetal.component.catalogue.common.evaluation;

import java.util.List;
import org.uma.jmetal.solution.Solution;

/**
 *  Interface representing entities that evaluate a list of solutions
 *
 *  @author Antonio J. Nebro <antonio@lcc.uma.es>
 *
 * @param <S> Solution
 */
public interface Evaluation<S extends Solution<?>> {
  List<S> evaluate(List<S> solutionList) ;
  int getComputedEvaluations() ;
}
