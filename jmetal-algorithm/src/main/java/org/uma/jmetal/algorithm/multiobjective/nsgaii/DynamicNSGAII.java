package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import java.util.List;
import org.uma.jmetal.algorithm.dynamic.DynamicAlgorithm;
import org.uma.jmetal.algorithm.dynamic.DynamicStoppingCondition;
import org.uma.jmetal.algorithm.dynamic.stepwise.StepwiseNSGAII;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.restartstrategy.RestartStrategy;

/**
 * Dynamic version of NSGA-II: a {@link DynamicAlgorithm} that runs NSGA-II one generation at a
 * time. By default the time of the problem advances with the generations (the protocol of the FDA
 * and CEC 2018 benchmarks), the run ends after 30 changes of the problem (as in the CEC 2018
 * competition), and the front is published at the end of every round; all of this can be changed
 * with the methods of {@link DynamicAlgorithm}.
 *
 * <p>Up to jMetal 7.11 this class ran forever, advanced the time of the problem once per round of
 * evaluations and restarted the population at the end of every round, whether the problem had
 * changed or not.
 *
 * @param <S> the type of the solutions
 */
@SuppressWarnings("serial")
public class DynamicNSGAII<S extends Solution<?>> extends DynamicAlgorithm<S> {
  public static final int DEFAULT_NUMBER_OF_PROBLEM_CHANGES = 30;

  /**
   * Creates the algorithm.
   *
   * @param problem the dynamic problem, whose counter is the generation
   * @param populationSize the population size, which is also the size of the mating pool
   * @param offspringPopulationSize the offspring population size
   * @param restartStrategy the restart strategy applied when the problem changes
   * @param generationsPerRound the generations of a round, at the end of which the front is
   *     published
   */
  public DynamicNSGAII(
      DynamicProblem<S, Integer> problem,
      int populationSize,
      int offspringPopulationSize,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator,
      SelectionOperator<List<S>, S> selectionOperator,
      SolutionListEvaluator<S> evaluator,
      RestartStrategy<S> restartStrategy,
      int generationsPerRound) {
    super(
        "DynamicNSGAII",
        new StepwiseNSGAII<>(
            problem,
            populationSize,
            populationSize,
            offspringPopulationSize,
            crossoverOperator,
            mutationOperator,
            selectionOperator,
            evaluator),
        problem,
        restartStrategy,
        generationsPerRound);
    timeDrivenByIterations(true);
    stoppingCondition(
        DynamicStoppingCondition.afterProblemChanges(DEFAULT_NUMBER_OF_PROBLEM_CHANGES));
  }
}
