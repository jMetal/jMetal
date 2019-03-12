package org.uma.jmetal.auto.component.evaluation.impl;

import org.uma.jmetal.auto.component.evaluation.Evaluation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.util.List;

public class MultithreadedEvaluation<S extends Solution<?>> implements Evaluation<S> {
  private Problem<S> problem ;

  public MultithreadedEvaluation(Problem<S> problem) {
    this.problem = problem ;
  }

 public List<S> evaluate(List<S> solutionList) {
   solutionList.parallelStream().forEach(solution ->problem.evaluate(solution));

   return solutionList ;
   }
}
