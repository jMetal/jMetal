package org.uma.jmetal.component.variation;

import org.uma.jmetal.solution.Solution;

import java.util.List;

public interface Variation<S extends Solution<?>> {
  List<S> variate(List<S> solutionList, List<S> matingPool) ;

  int getMatingPoolSize() ;
  int getOffspringPopulationSize() ;
}
