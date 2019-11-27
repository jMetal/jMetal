package org.uma.jmetal.auto.component.evaluation;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.observable.ObservableEntity;

import java.util.List;

public interface Evaluation<S extends Solution<?>> extends ObservableEntity {
  List<S> evaluate(List<S> solutionList) ;
}
