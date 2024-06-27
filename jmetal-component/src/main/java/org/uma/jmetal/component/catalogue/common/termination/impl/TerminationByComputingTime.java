package org.uma.jmetal.component.catalogue.common.termination.impl;

import java.util.Map;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Class that allows to check the termination condition when the computing time of an algorithm
 * gets higher than a given threshold.
 *
 *  @author Antonio J. Nebro
 */
public class TerminationByComputingTime implements Termination {
  private final long maxComputingTime ;
  private int evaluations ;

  public TerminationByComputingTime(long maxComputingTime) {
    this.maxComputingTime = maxComputingTime ;
  }

  @Override
  public boolean isMet(Map<String, Object> algorithmStatusData) {
    Check.notNull(algorithmStatusData.get("COMPUTING_TIME"));
    Check.notNull(algorithmStatusData.get("EVALUATIONS"));

    long currentComputingTime = (long) algorithmStatusData.get("COMPUTING_TIME") ;
    evaluations = (int) algorithmStatusData.get("EVALUATIONS") ;

    return currentComputingTime >= maxComputingTime ;
  }

  public int getEvaluations() {
    return evaluations ;
  }

  public long getMaxComputingTime() {
    return maxComputingTime ;
  }
}
