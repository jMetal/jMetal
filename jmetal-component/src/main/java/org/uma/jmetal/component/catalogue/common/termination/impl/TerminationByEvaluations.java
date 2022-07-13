package org.uma.jmetal.component.catalogue.common.termination.impl;

import java.util.Map;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Class that allows to check the termination condition based on a maximum number of indicated
 * evaluations.
 *
 *  @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class TerminationByEvaluations implements Termination {
  private final int maximumNumberOfEvaluations ;

  public TerminationByEvaluations(int maximumNumberOfEvaluations) {
    this.maximumNumberOfEvaluations = maximumNumberOfEvaluations ;
  }

  @Override
  public boolean isMet(Map<String, Object> algorithmStatusData) {
    Check.notNull(algorithmStatusData.get("EVALUATIONS") );
    int currentNumberOfEvaluations = (int) algorithmStatusData.get("EVALUATIONS") ;

    return (currentNumberOfEvaluations >= maximumNumberOfEvaluations) ;
  }

  public int getMaximumNumberOfEvaluations() {
    return maximumNumberOfEvaluations ;
  }
}
