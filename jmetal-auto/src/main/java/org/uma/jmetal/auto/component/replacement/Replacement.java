package org.uma.jmetal.auto.component.replacement;

import org.uma.jmetal.solution.Solution;

import java.util.List;

public interface Replacement<S extends Solution<?>> {
  List<S> replace(List<S> currentList, List<S> offspringList) ;
}
