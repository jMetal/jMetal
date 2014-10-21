package org.uma.jmetal.util.measurement;

/**
 * Created by Antonio J. Nebro on 21/10/14 based on the ideas of Matthieu Vergne
 */
public interface PullMeasure<T> extends Measure {
  // Request immediately the current value of the measure
  public T get();
}
