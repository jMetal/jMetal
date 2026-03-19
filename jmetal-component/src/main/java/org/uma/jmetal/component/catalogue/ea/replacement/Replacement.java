package org.uma.jmetal.component.catalogue.ea.replacement;

import java.util.List;
import org.uma.jmetal.solution.Solution;

@FunctionalInterface
public interface Replacement<S extends Solution<?>> {
  List<S> replace(List<S> currentList, List<S> offspringList) ;
}
