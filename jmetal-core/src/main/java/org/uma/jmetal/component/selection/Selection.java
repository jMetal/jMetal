package org.uma.jmetal.component.selection;

import org.uma.jmetal.solution.Solution;

import java.util.List;

@FunctionalInterface
public interface Selection<S extends Solution<?>> {
  List<S> select(List<S> solutionList) ;
}
