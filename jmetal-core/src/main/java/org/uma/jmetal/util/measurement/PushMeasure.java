package org.uma.jmetal.util.measurement;

/**
 * Created by Antonio J. Nebro on 21/10/14 based on the ideas of Matthieu Vergne
 */
public interface PushMeasure<T> extends Measure {
  // Register to obtain the measure when it is computed
  public void register(MeasureListener<T> listener);
  // Unregister to free the measure when it is not needed anymore
  public void unregister(MeasureListener<T> listener);
}
