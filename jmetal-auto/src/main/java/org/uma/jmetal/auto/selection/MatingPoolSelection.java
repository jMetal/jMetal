package org.uma.jmetal.auto.selection;

import org.uma.jmetal.solution.Solution;

import java.util.List;

public interface MatingPoolSelection<S extends Solution<?>> {
  List<S> select(List<S> solutionList) ;
}
