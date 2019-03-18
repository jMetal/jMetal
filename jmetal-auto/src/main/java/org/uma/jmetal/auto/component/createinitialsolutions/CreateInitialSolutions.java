package org.uma.jmetal.auto.component.createinitialsolutions;

import org.uma.jmetal.solution.Solution;

import java.util.List;

@FunctionalInterface
public interface CreateInitialSolutions<S extends Solution<?>> {
  List<S> create() ;
}
