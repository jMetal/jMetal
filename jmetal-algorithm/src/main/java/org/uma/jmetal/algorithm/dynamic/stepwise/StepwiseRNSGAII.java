package org.uma.jmetal.algorithm.dynamic.stepwise;

import java.util.List;
import org.uma.jmetal.algorithm.dynamic.InteractiveStepwiseAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.rnsgaii.RNSGAII;
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
 * R-NSGA-II run one generation at a time by a {@link
 * org.uma.jmetal.algorithm.dynamic.DynamicAlgorithm}, with one or more reference points that can
 * change during the run.
 *
 * @param <S> the type of the solutions
 */
@SuppressWarnings("serial")
public class StepwiseRNSGAII<S extends Solution<?>> extends RNSGAII<S>
    implements InteractiveStepwiseAlgorithm<S> {
  private List<List<Double>> referencePoints;

  /**
   * Creates the algorithm.
   *
   * @param referencePoints the reference points, each one with a value per objective
   * @param epsilon the epsilon of R-NSGA-II, which controls the spread of the solutions around
   *     each reference point
   */
  public StepwiseRNSGAII(
      Problem<S> problem,
      int populationSize,
      int matingPoolSize,
      int offspringPopulationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator,
      SolutionListEvaluator<S> evaluator,
      List<List<Double>> referencePoints,
      double epsilon) {
    super(
        problem,
        Integer.MAX_VALUE,
        populationSize,
        matingPoolSize,
        offspringPopulationSize,
        crossoverOperator,
        mutationOperator,
        selectionOperator,
        evaluator,
        flatten(referencePoints, problem.numberOfObjectives()),
        epsilon);
    this.referencePoints = List.copyOf(referencePoints);
  }

  private static List<Double> flatten(List<List<Double>> referencePoints, int numberOfObjectives) {
    Check.that(!referencePoints.isEmpty(), "There must be at least one reference point");
    referencePoints.forEach(
        point ->
            Check.that(
                point.size() == numberOfObjectives,
                () -> "A reference point must have " + numberOfObjectives + " values: " + point));
    return referencePoints.stream().flatMap(List::stream).toList();
  }

  @Override
  public int initialize() {
    setPopulation(evaluatePopulation(createInitialPopulation()));
    return getPopulation().size();
  }

  @Override
  public int step() {
    List<S> matingPopulation = selection(getPopulation());
    List<S> offspringPopulation = evaluatePopulation(reproduction(matingPopulation));
    setPopulation(replacement(getPopulation(), offspringPopulation));
    return offspringPopulation.size();
  }

  @Override
  public int restart(RestartStrategy<S> restartStrategy, DynamicProblem<S, ?> problem) {
    restartStrategy.restart(getPopulation(), problem);
    setPopulation(evaluatePopulation(getPopulation()));
    return getPopulation().size();
  }

  @Override
  public void updateReferencePoints(List<List<Double>> referencePoints) {
    updatePointOfInterest(flatten(referencePoints, getProblem().numberOfObjectives()));
    this.referencePoints = List.copyOf(referencePoints);
  }

  @Override
  public List<List<Double>> referencePoints() {
    return referencePoints;
  }

  @Override
  public boolean acceptsNumberOfReferencePoints(int numberOfReferencePoints) {
    return numberOfReferencePoints >= 1;
  }

  @Override
  public List<S> population() {
    return getPopulation();
  }
}
