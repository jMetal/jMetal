package org.uma.jmetal.util.measurement;

import java.util.Collection;

/**
 * Created by Antonio J. Nebro on 21/10/14 based on the ideas of Matthieu Vergne
 */
public interface MeasureManager {
  public Collection<PullMeasure<?>> getPullMeasures();
  public Collection<PushMeasure<?>> getPushMeasures();
}