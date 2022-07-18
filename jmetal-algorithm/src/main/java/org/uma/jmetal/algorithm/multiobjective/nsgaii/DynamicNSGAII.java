package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.uma.jmetal.algorithm.DynamicAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.util.CoverageFront;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetal.util.restartstrategy.RestartStrategy;

@SuppressWarnings("serial")
public class DynamicNSGAII<S extends Solution<?>> extends NSGAII<S>
    implements DynamicAlgorithm<List<S>> {

  private RestartStrategy<S> restartStrategy;
  private DynamicProblem<S, Integer> problem;
  private Observable<Map<String, Object>> observable;
  private int completedIterations;
  private CoverageFront coverageFront;
  private List<S> lastReceivedFront;
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
      Observable<Map<String, Object>> observable,
      CoverageFront coverageFront) {
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
    this.coverageFront = coverageFront;
    this.lastReceivedFront = null;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    if (evaluations >= maxEvaluations) {

      boolean coverage = false;
      if (lastReceivedFront != null) {
        coverageFront.updateFront(SolutionListUtils.getMatrixWithObjectiveValues(lastReceivedFront));
        List<PointSolution> pointSolutionList;
        List<S> list = getPopulation();
          pointSolutionList = list.stream().map(PointSolution::new).collect(Collectors.toList());
        coverage = coverageFront.isCoverageWithLast(pointSolutionList);
      }

      if (coverage) {
        observable.setChanged();

        Map<String, Object> algorithmData = new HashMap<>();

        algorithmData.put("EVALUATIONS", completedIterations);
        algorithmData.put("POPULATION", getPopulation());

        observable.notifyObservers(algorithmData);
        observable.clearChanged();
      }
      lastReceivedFront = getPopulation();
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
