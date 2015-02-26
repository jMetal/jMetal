package org.uma.jmetal.algorithm.totrythemeasures;

import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class NSGAIIM extends NSGAII {
  private CountingMeasure iterations ;
  //private DurationMeasure durationMeasure ;
  //private SimpleMeasureManager measureManager ;

  /**
   * Constructor
   */
  public NSGAIIM(Problem problem, int maxIterations, int populationSize,
      CrossoverOperator crossoverOperator, MutationOperator mutationOperator,
      SelectionOperator selectionOperator, SolutionListEvaluator evaluator) {
    super(problem, maxIterations, populationSize, crossoverOperator, mutationOperator, selectionOperator, evaluator) ;

    initMeasures() ;
  }

  @Override protected void initProgress() {
    iterations.reset(1);
  }

  @Override protected void updateProgress() {
    iterations.increment();
  }

  @Override protected boolean isStoppingConditionReached() {
    return iterations.get() >= maxIterations;
  }

  @Override
  public void run() {
    durationMeasure.reset();
    durationMeasure.start();
    super.run();
    durationMeasure.stop();
  }

  /* Measures code */
  private void initMeasures() {
    //durationMeasure = new DurationMeasure() ;
    iterations = new CountingMeasure(0) ;

    //measureManager = new SimpleMeasureManager() ;
    //measureManager.setPullMeasure("currentExecutionTime", durationMeasure);
    measureManager.setPullMeasure("currentIteration", iterations);
  }

  @Override
  public MeasureManager getMeasureManager() {
    return measureManager ;
  }
}
