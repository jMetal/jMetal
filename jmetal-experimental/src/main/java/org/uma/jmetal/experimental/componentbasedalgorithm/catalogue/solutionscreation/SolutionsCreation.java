package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.solutionscreation;

import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Interface representing entities that create a list of solutions applying some strategy (e.g, random)
 *
 * @param <S>
 */
@FunctionalInterface
public interface SolutionsCreation<S extends Solution<?>> {
  List<S> create() ;
}
