package org.uma.jmetal.util.measurement;

/**
 * Created by Antonio J. Nebro on 21/10/14 based on the ideas of Matthieu Vergne
 */
public interface Measure {
  // Name of the measure, e.g. "population size"
  public String getName();
  // Description of the measure, e.g. "Provide the current size of the population of individuals."
  public String getDescription();
}

