package org.uma.jmetal.experiment;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

/**
 * Created by ajnebro on 3/1/15.
 */
public interface AlgorithmBuilder2<S extends Solution<?>, P extends Problem<S>> {
  public Algorithm<?> build(P problem) ;
  public P getProblem() ;
}
