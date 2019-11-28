package org.uma.jmetal.solution.util.attribute;

import org.uma.jmetal.solution.Solution;

import java.util.Comparator;

public interface Attribute<S extends Solution<?>> {
  String getAttributeId() ;
  Comparator<S> getSolutionComparator() ;
}
