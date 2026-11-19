package org.uma.jmetal.algorithm.dynamic.stepwise;

import java.util.List;
import org.uma.jmetal.algorithm.dynamic.StepwiseAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.restartstrategy.RestartStrategy;

/**
 * NSGA-II run one generation at a time by a {@link
 * org.uma.jmetal.algorithm.dynamic.DynamicAlgorithm}.
 *
 * @param <S> the type of the solutions
 */
@SuppressWarnings("serial")
public class StepwiseNSGAII<S extends Solution<?>> extends NSGAII<S>
    implements StepwiseAlgorithm<S> {

  public StepwiseNSGAII(
      Problem<S> problem,
      int populationSize,
      int matingPoolSize,
      int offspringPopulationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator,
      SolutionListEvaluator<S> evaluator) {
    super(
        problem,
        Integer.MAX_VALUE,
        populationSize,
        matingPoolSize,
        offspringPopulationSize,
        crossoverOperator,
        mutationOperator,
        selectionOperator,
        evaluator);
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
  public List<S> population() {
    return getPopulation();
  }
}
