package org.uma.jmetal.auto.pruebas.operator;

import java.util.List;
import org.uma.jmetal.auto.pruebas.solution.Solution2;

/**
 * Interface representing crossover operators. They will receive a list of solutions and return
 * another list of solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 *
 */
public interface Crossover2<S extends Solution2> {
  double getCrossoverProbability() ;

  int getNumberOfRequiredParents() ;
  int getNumberOfGeneratedChildren() ;

  List<S> execute(List<S> source) ;

}
