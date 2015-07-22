package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import org.uma.jmetal.measure.Measurable;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.*;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIMeasures<S extends Solution<?>> extends NSGAII<S> implements Measurable {
  private CountingMeasure iterations ;
  private BasicMeasure<Integer> numberOfFeasibleSolutionsInPopulation ;
  private DurationMeasure durationMeasure ;
  private SimpleMeasureManager measureManager ;

  private BasicMeasure<List<S>> solutionListMeasure ;
  private BasicMeasure<Integer> numberOfNonDominatedSolutionsInPopulation ;


  /**
   * Constructor
   */
  public NSGAIIMeasures(Problem<S> problem, int maxIterations, int populationSize,
      CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator) {
    super(problem, maxIterations, populationSize, crossoverOperator, mutationOperator,
        selectionOperator, evaluator) ;

    initMeasures() ;
  }

  @Override protected void initProgress() {
    iterations.reset(1);
  }

  @Override protected void updateProgress() {
    iterations.increment();

    solutionListMeasure.push(getPopulation());
  }

  @Override protected boolean isStoppingConditionReached() {
    return iterations.get() >= maxIterations;
  }

  @Override protected List<S> evaluatePopulation(List<S> population) {
    population = super.evaluatePopulation(population);

    int countFeasibleSolutions = 0 ;
    for (S solution : population) {
      if (solution.getOverallConstraintViolationDegree() == 0) {
        countFeasibleSolutions ++ ;
      }
    }

    if (countFeasibleSolutions > 0) {
      numberOfFeasibleSolutionsInPopulation.push(countFeasibleSolutions);
    }

    return population;
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
    durationMeasure = new DurationMeasure() ;
    iterations = new CountingMeasure(0) ;
    numberOfNonDominatedSolutionsInPopulation = new BasicMeasure<>() ;
    solutionListMeasure = new BasicMeasure<>() ;
    numberOfFeasibleSolutionsInPopulation = new BasicMeasure<>() ;

    measureManager = new SimpleMeasureManager() ;
    measureManager.setPullMeasure("currentExecutionTime", durationMeasure);
    measureManager.setPullMeasure("currentIteration", iterations);
    measureManager.setPullMeasure("numberOfNonDominatedSolutionsInPopulation",
        numberOfNonDominatedSolutionsInPopulation);

    measureManager.setPushMeasure("currentPopulation", solutionListMeasure);
    measureManager.setPushMeasure("currentIteration", iterations);
    measureManager.setPushMeasure("numberOfFeasibleSolutionsInPopulation",
        numberOfFeasibleSolutionsInPopulation);
  }

  @Override
  public MeasureManager getMeasureManager() {
    return measureManager ;
  }

  @Override protected List<S> replacement(List<S> population,
      List<S> offspringPopulation) {
    List<S> pop = super.replacement(population, offspringPopulation) ;

    Ranking<S> ranking = computeRanking(pop);
    numberOfNonDominatedSolutionsInPopulation.set(ranking.getSubfront(0).size());

    return pop;
  }
}
