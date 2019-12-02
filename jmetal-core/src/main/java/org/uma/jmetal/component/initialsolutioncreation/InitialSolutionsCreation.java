package org.uma.jmetal.component.initialsolutioncreation;

import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Interface representing entities that create a list of solutions applying some strategy (e.g, random)
 *
 * @param <S>
 */
@FunctionalInterface
public interface InitialSolutionsCreation<S extends Solution<?>> {
  List<S> create() ;
}
