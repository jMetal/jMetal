package org.uma.jmetal.component.termination.impl;

import org.uma.jmetal.component.termination.Termination;

import java.util.Map;

/**
 * Class that allows to isMet the termination condition when the computing time of an algorithm
 * gets higher than a given threshold.
 *
 *  @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class TerminationByComputingTime implements Termination {
  private long maxComputingTime ;
  private int evaluations ;

  public TerminationByComputingTime(int maxComputingTime) {
    this.maxComputingTime = maxComputingTime ;
    this.evaluations = 0 ;
  }

  @Override
  public boolean isMet(Map<String, Object> algorithmStatusData) {
    long currentComputingTime = (long) algorithmStatusData.get("COMPUTING_TIME") ;

    return currentComputingTime >= maxComputingTime ;
  }

  public int getEvaluations() {
    return evaluations ;
  }
}
