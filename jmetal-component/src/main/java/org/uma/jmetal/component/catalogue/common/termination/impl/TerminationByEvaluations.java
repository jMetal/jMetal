package org.uma.jmetal.component.catalogue.common.termination.impl;

import java.util.Map;
import java.util.OptionalDouble;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Class that allows to check the termination condition based on a maximum number of indicated
 * evaluations.
 *
 *  @author Antonio J. Nebro
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

  /** The evaluations done over the maximum, or empty if the status has no evaluations. */
  @Override
  public OptionalDouble progress(Map<String, Object> algorithmStatusData) {
    Object evaluations = algorithmStatusData.get("EVALUATIONS");
    if (evaluations == null) {
      return OptionalDouble.empty();
    }
    return OptionalDouble.of(
        Math.min(1.0, ((int) evaluations) / (double) maximumNumberOfEvaluations));
  }

  public int getMaximumNumberOfEvaluations() {
    return maximumNumberOfEvaluations ;
  }
}
