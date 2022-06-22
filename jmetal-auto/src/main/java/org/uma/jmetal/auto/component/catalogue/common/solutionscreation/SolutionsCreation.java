package org.uma.jmetal.auto.component.catalogue.common.solutionscreation;

import java.util.List;
import org.uma.jmetal.solution.Solution;

/**
 * Interface representing entities that create a list of solutions applying some strategy (e.g, random)
 *
 * @param <S>
 */
@FunctionalInterface
public interface SolutionsCreation<S extends Solution<?>> {
  List<S> create() ;
}
