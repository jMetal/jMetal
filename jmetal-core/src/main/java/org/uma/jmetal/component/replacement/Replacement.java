package org.uma.jmetal.component.replacement;

import org.uma.jmetal.solution.Solution;

import java.util.List;

@FunctionalInterface
public interface Replacement<S extends Solution<?>> {
  enum RemovalPolicy {sequential, oneShot}
  List<S> replace(List<S> currentList, List<S> offspringList) ;
}
