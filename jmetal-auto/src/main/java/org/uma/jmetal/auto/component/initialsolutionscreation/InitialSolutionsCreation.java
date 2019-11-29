package org.uma.jmetal.auto.component.initialsolutionscreation;

import org.uma.jmetal.solution.Solution;

import java.util.List;

@FunctionalInterface
public interface InitialSolutionsCreation<S extends Solution<?>> {
  List<S> create() ;
}
