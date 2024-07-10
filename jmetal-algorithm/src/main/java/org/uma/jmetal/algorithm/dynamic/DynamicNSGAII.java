package org.uma.jmetal.algorithm.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.dynamic.util.CoverageFront;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.pointsolution.PointSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.restartstrategy.RestartStrategy;

@SuppressWarnings("serial")
public class DynamicNSGAII<S extends Solution<?>> extends NSGAII<S> {

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
      CoverageFront<PointSolution> coverageFront) {
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
        List<PointSolution> pointSolutionList = new ArrayList<>();
        List<S> list = getPopulation();
        for (S s : list) {
          PointSolution pointSolution = new PointSolution(s);
          pointSolutionList.add(pointSolution);
        }
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

  public DynamicProblem<S, ?> getDynamicProblem() {
    return problem;
  }

  public void restart() {
    this.restartStrategy.restart(getPopulation(), (DynamicProblem<S, ?>) getProblem());
  }

  public RestartStrategy<?> getRestartStrategy() {
    return restartStrategy;
  }

  public Observable<Map<String, Object>> getObservable() {
    return observable;
  }
}
