package org.uma.jmetal.util.solutionattribute.impl;

import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.solution.Solution;

@SuppressWarnings("serial")
public class SolutionTextRepresentation extends GenericSolutionAttribute<Solution<?>,String>{

  private static @Nullable SolutionTextRepresentation singleInstance = null;
  private SolutionTextRepresentation() {}

  public static SolutionTextRepresentation getAttribute() {
    if (singleInstance == null)
      singleInstance = new SolutionTextRepresentation();
    return singleInstance;
  }
}
