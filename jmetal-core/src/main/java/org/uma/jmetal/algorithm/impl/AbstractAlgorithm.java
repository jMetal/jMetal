package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.measure.impl.DurationMeasure;
import org.uma.jmetal.measure.impl.SimpleMeasureManager;

/**
 * Created by ajnebro on 25/2/15.
 */
public abstract class AbstractAlgorithm<R> implements Algorithm<R> {
  private SimpleMeasureManager measureManager ;
  private DurationMeasure durationMeasure ;

  public AbstractAlgorithm() {
    durationMeasure = new DurationMeasure() ;

    measureManager = new SimpleMeasureManager() ;
    measureManager.setPullMeasure("currentExecutionTime", durationMeasure);
  }
}
