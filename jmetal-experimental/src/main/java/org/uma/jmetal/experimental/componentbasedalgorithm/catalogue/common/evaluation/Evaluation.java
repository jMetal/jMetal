package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation;

import org.uma.jmetal.solution.Solution;

import java.util.List;

public interface Evaluation<S extends Solution<?>> {
  List<S> evaluate(List<S> solutionList) ;
  int getComputedEvaluations() ;
}
