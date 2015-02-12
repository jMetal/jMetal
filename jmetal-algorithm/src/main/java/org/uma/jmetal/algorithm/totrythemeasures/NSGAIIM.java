package org.uma.jmetal.algorithm.totrythemeasures;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.measurement.impl.CountingMeasure;
import org.uma.jmetal.measurement.impl.DurationMeasure;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class NSGAIIM extends NSGAII {
  private CountingMeasure iterations ;
  private DurationMeasure durationMeasure ;

  /**
   * Constructor
   */
  public NSGAIIM(Problem problem, int maxIterations, int populationSize,
      CrossoverOperator crossoverOperator, MutationOperator mutationOperator,
      SelectionOperator selectionOperator, SolutionListEvaluator evaluator) {
    super(problem, maxIterations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator) ;

    durationMeasure = new DurationMeasure() ;
    durationMeasure.start();
  }

  @Override protected void initProgress() {
    iterations = new CountingMeasure(1) ;
  }

  @Override protected void updateProgress() {
    iterations.increment();
  }

  @Override protected boolean isStoppingConditionReached() {
    return iterations.get() >= maxIterations;
  }

}
