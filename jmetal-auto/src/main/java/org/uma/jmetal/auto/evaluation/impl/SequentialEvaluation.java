package org.uma.jmetal.auto.evaluation.impl;

import org.uma.jmetal.auto.evaluation.Evaluation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.util.List;

public class SequentialEvaluation<S extends Solution<?>> implements Evaluation<S> {
  private Problem<S> problem ;

  public SequentialEvaluation(Problem<S> problem) {
    this.problem = problem ;
  }

 public List<S> evaluate(List<S> solutionList) {
   solutionList.forEach(solution ->problem.evaluate(solution));

   return solutionList ;
   }
}
