package org.uma.jmetal.problem;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.observer.Observer;

/**
 * Created by ajnebro on 18/4/16.
 */
public interface DynamicProblem<S extends Solution<?>, D>
    extends Problem<S>, Observer<D> {

  boolean hasChanged() ;
  void setChanged() ;
  void clearChanged() ;
}