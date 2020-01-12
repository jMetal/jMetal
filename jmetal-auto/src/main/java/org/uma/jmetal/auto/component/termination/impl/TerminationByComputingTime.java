package org.uma.jmetal.auto.component.termination.impl;

import org.uma.jmetal.auto.component.termination.Termination;
import org.uma.jmetal.util.JMetalLogger;

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

    boolean result = currentComputingTime >= maxComputingTime ;
    if (result) {
      this.evaluations = (int)algorithmStatusData.get("EVALUATIONS") ;
      JMetalLogger.logger.info("Evaluations: " + evaluations);
    }

    return result ;
  }

  public int getEvaluations() {
    return evaluations ;
  }
}
