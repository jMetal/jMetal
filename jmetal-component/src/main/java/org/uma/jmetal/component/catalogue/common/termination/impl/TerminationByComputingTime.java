package org.uma.jmetal.component.catalogue.common.termination.impl;

import java.util.Map;
import java.util.OptionalDouble;
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

  /** The computing time over the maximum, or empty if the status has no computing time. */
  @Override
  public OptionalDouble progress(Map<String, Object> algorithmStatusData) {
    Object computingTime = algorithmStatusData.get("COMPUTING_TIME");
    if (computingTime == null) {
      return OptionalDouble.empty();
    }
    return OptionalDouble.of(Math.min(1.0, ((long) computingTime) / (double) maxComputingTime));
  }

  public int getEvaluations() {
    return evaluations ;
  }

  public long getMaxComputingTime() {
    return maxComputingTime ;
  }
}
