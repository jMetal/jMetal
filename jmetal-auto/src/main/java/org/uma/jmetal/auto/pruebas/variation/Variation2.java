package org.uma.jmetal.auto.pruebas.variation;

import java.util.List;
import org.uma.jmetal.auto.pruebas.solution.Solution2;

public interface Variation2<S extends Solution2> {
  List<S> variate(List<S> solutionList, List<S> matingPool) ;

  int getMatingPoolSize() ;
  int getOffspringPopulationSize() ;
}
