package org.uma.jmetal.algorithm.dynamic.stepwise;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.dynamic.InteractiveStepwiseAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.wasfga.WASFGA;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.restartstrategy.RestartStrategy;

/**
 * WASF-GA run one generation at a time by a {@link
 * org.uma.jmetal.algorithm.dynamic.DynamicAlgorithm}, with a reference point that can change during
 * the run. WASF-GA accepts a single reference point.
 *
 * @param <S> the type of the solutions
 */
@SuppressWarnings("serial")
public class StepwiseWASFGA<S extends Solution<?>> extends WASFGA<S>
    implements InteractiveStepwiseAlgorithm<S> {
  private List<List<Double>> referencePoints;

  /**
   * Creates the algorithm.
   *
   * @param populationSize the population size, which is also the number of weight vectors
   * @param epsilon the epsilon used to compute the weight vectors with two objectives
   * @param referencePoint the reference point, with a value per objective
   */
  public StepwiseWASFGA(
      Problem<S> problem,
      int populationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator,
      SolutionListEvaluator<S> evaluator,
      double epsilon,
      List<Double> referencePoint) {
    super(
        problem,
        populationSize,
        Integer.MAX_VALUE,
        crossoverOperator,
        mutationOperator,
        selectionOperator,
        evaluator,
        epsilon,
        checkedPoint(referencePoint, problem.numberOfObjectives()));
    this.referencePoints = List.of(List.copyOf(referencePoint));
  }

  /**
   * A modifiable copy of the point, because the achievement scalarizing function of WASF-GA keeps
   * the list that it receives and changes its values when the point changes.
   */
  private static List<Double> checkedPoint(List<Double> point, int numberOfObjectives) {
    Check.notNull(point);
    Check.that(
        point.size() == numberOfObjectives,
        () -> "The reference point must have " + numberOfObjectives + " values: " + point);
    return new ArrayList<>(point);
  }

  @Override
  public int initialize() {
    setPopulation(evaluatePopulation(createInitialPopulation()));
    specificMOEAComputations();
    return getPopulation().size();
  }

  @Override
  public int step() {
    List<S> matingPopulation = selection(getPopulation());
    List<S> offspringPopulation = evaluatePopulation(reproduction(matingPopulation));
    setPopulation(replacement(getPopulation(), offspringPopulation));
    specificMOEAComputations();
    return offspringPopulation.size();
  }

  /** Also forgets the ideal and nadir points estimated with the objectives before the change. */
  @Override
  public int restart(RestartStrategy<S> restartStrategy, DynamicProblem<S, ?> problem) {
    restartStrategy.restart(getPopulation(), problem);
    setPopulation(evaluatePopulation(getPopulation()));
    for (int i = 0; i < getProblem().numberOfObjectives(); i++) {
      setReferencePointValue(Double.POSITIVE_INFINITY, i);
      getNadirPoint().set(i, Double.NEGATIVE_INFINITY);
    }
    specificMOEAComputations();
    return getPopulation().size();
  }

  @Override
  public void updateReferencePoints(List<List<Double>> referencePoints) {
    Check.that(
        referencePoints.size() == 1,
        () -> "WASF-GA accepts a single reference point: " + referencePoints);
    updatePointOfInterest(
        checkedPoint(referencePoints.get(0), getProblem().numberOfObjectives()));
    this.referencePoints = List.of(List.copyOf(referencePoints.get(0)));
  }

  @Override
  public List<List<Double>> referencePoints() {
    return referencePoints;
  }

  @Override
  public boolean acceptsNumberOfReferencePoints(int numberOfReferencePoints) {
    return numberOfReferencePoints == 1;
  }

  @Override
  public List<S> population() {
    return getPopulation();
  }
}
