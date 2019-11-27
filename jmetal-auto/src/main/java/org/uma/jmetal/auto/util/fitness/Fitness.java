package org.uma.jmetal.auto.util.fitness;

import org.uma.jmetal.solution.Solution;

import java.util.List;

public interface Fitness<S extends Solution<?>> {
  double getFitness(S solution) ;
  void computeFitness(List<S> solutionList) ;
}
