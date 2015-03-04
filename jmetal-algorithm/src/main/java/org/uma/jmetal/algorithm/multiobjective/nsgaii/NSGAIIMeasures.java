package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import org.uma.jmetal.measurement.MeasureManager;
import org.uma.jmetal.measurement.impl.CountingMeasure;
import org.uma.jmetal.measurement.impl.DurationMeasure;
import org.uma.jmetal.measurement.impl.SimpleMeasureManager;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class NSGAIIMeasures extends NSGAII {
  private CountingMeasure iterations ;
  private DurationMeasure durationMeasure ;
  private SimpleMeasureManager measureManager ;

  /**
   * Constructor
   */
  public NSGAIIMeasures(Problem problem, int maxIterations, int populationSize,
      CrossoverOperator crossoverOperator, MutationOperator mutationOperator,
      SelectionOperator selectionOperator, SolutionListEvaluator evaluator) {
    super(problem, maxIterations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator) ;

    initMeasures() ;
  }

  @Override protected void initProgress() {
    iterations.increment(); ;
  }

  @Override protected void updateProgress() {
    iterations.increment();
  }

  @Override protected boolean isStoppingConditionReached() {
    return iterations.get() >= maxIterations;
  }

  private void initMeasures() {
    durationMeasure = new DurationMeasure() ;
    iterations = new CountingMeasure(0) ;

    measureManager = new SimpleMeasureManager() ;
    measureManager.setMeasure("currentExecutionTime", durationMeasure);
    measureManager.setMeasure("currentIteration", iterations);

    durationMeasure.start();
  }

  public MeasureManager getMeasureManager() {
    return measureManager ;
  }
}
