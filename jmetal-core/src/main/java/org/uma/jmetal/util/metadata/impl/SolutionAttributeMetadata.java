package org.uma.jmetal.util.metadata.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.metadata.Metadata;

public class SolutionAttributeMetadata<S extends Solution<?>, V> implements Metadata.RW<S, V> {

  private final String valueName;

  public SolutionAttributeMetadata(String valueName) {
    this.valueName = valueName;
  }

  @SuppressWarnings("unchecked")
  @Override
  public V read(S solution) {
    V value = (V) solution.getAttribute(this);
    if (value == null) {
      throw new IllegalStateException("No " + valueName + " in attributes of " + solution);
    }
    return value;
  }

  @Override
  public void write(S solution, V value) {
    solution.setAttribute(this, value);
  }
}
