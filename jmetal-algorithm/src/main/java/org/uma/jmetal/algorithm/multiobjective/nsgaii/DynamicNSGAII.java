package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import org.uma.jmetal.algorithm.DynamicAlgorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.restartstrategy.RestartStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamicNSGAII<S extends Solution<?>> extends NSGAII<S>
    implements DynamicAlgorithm<List<S>> {

  private RestartStrategy<S> restartStrategy;
  private DynamicProblem<S, Integer> problem;
  private Observable<Map<String, Object>> observable;
  private int completedIterations;

  /**
   * Constructor
   *
   * @param problem
   * @param maxEvaluations
   * @param populationSize
   * @param matingPoolSize
   * @param offspringPopulationSize
   * @param crossoverOperator
   * @param mutationOperator
   * @param selectionOperator
   * @param evaluator
   * @param restartStrategy
   * @param observable
   */
  public DynamicNSGAII(
      DynamicProblem<S, Integer> problem,
      int maxEvaluations,
      int populationSize,
      int matingPoolSize,
      int offspringPopulationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator,
      SolutionListEvaluator<S> evaluator,
      RestartStrategy<S> restartStrategy,
      Observable<Map<String, Object>> observable) {
    super(
        problem,
        maxEvaluations,
        populationSize,
        matingPoolSize,
        offspringPopulationSize,
        crossoverOperator,
        mutationOperator,
        selectionOperator,
        evaluator);
    this.restartStrategy = restartStrategy;
    this.problem = problem;
    this.observable = observable;
    this.completedIterations = 0;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    if (evaluations >= maxEvaluations) {
      observable.setChanged();

      Map<String, Object> algorithmData = new HashMap<>();

      algorithmData.put("EVALUATIONS", completedIterations);
      algorithmData.put("POPULATION", getPopulation());

      observable.notifyObservers(algorithmData);
      observable.clearChanged();
      completedIterations++;
      problem.update(completedIterations);

      restart();
      evaluator.evaluate(getPopulation(), getDynamicProblem());

      initProgress();
    }
    return false;
  }

  @Override
  protected void updateProgress() {
    super.updateProgress();
  }

  @Override
  public DynamicProblem<S, ?> getDynamicProblem() {
    return problem;
  }

  @Override
  public void restart() {
    this.restartStrategy.restart(getPopulation(), (DynamicProblem<S, ?>) getProblem());
  }

  @Override
  public RestartStrategy<?> getRestartStrategy() {
    return restartStrategy;
  }

  @Override
  public Observable<Map<String, Object>> getObservable() {
    return observable;
  }
}
