package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.evaluation;

import java.util.List;
import org.uma.jmetal.solution.Solution;

public interface Evaluation<S extends Solution<?>> {
  List<S> evaluate(List<S> solutionList) ;
  int getComputedEvaluations() ;
}
