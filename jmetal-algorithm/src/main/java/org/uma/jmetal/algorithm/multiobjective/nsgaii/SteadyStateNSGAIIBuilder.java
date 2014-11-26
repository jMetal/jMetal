package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

/**
 * Created by ajnebro on 16/11/14.
 */
public class SteadyStateNSGAIIBuilder extends NSGAIIBuilder {

  /** SteadyStateNSGAIIBuilder constructor */
  public SteadyStateNSGAIIBuilder(Problem problem) {
    super(problem); ;
    maxIterations = 250 ;
    populationSize = 100 ;
    evaluator = new SequentialSolutionListEvaluator() ;
  }

  @Override
  public SteadyStateNSGAII build() {
    return new SteadyStateNSGAII(problem, maxIterations, populationSize, crossoverOperator, mutationOperator,
            selectionOperator, evaluator) ;
  }
}
