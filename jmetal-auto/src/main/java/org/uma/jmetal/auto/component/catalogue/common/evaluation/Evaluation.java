package org.uma.jmetal.auto.component.catalogue.common.evaluation;

import java.util.List;
import org.uma.jmetal.solution.Solution;

public interface Evaluation<S extends Solution<?>> {
  List<S> evaluate(List<S> solutionList) ;
  int getComputedEvaluations() ;
}
