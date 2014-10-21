package org.uma.jmetal.util.measurement;

/**
 * Created by Antonio J. Nebro on 21/10/14 based on the ideas of Matthieu Vergne
 */
public interface MeasureListener<T> {
  public void measureGenerated(T value);
}