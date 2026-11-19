package org.uma.jmetal.algorithm.multiobjective.indm2;

import org.uma.jmetal.algorithm.dynamic.DynamicStoppingCondition;
import org.uma.jmetal.algorithm.dynamic.InteractiveStepwiseAlgorithm;
import org.uma.jmetal.problem.DynamicProblem;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.restartstrategy.RestartStrategy;
import org.uma.jmetal.util.restartstrategy.impl.CreateNRandomSolutions;
import org.uma.jmetal.util.restartstrategy.impl.DefaultRestartStrategy;
import org.uma.jmetal.util.restartstrategy.impl.RemoveFirstNSolutions;
import org.uma.jmetal.util.restartstrategy.impl.RemoveNSolutionsAccordingToTheHypervolumeContribution;

/**
 * Builder of {@link InDM2}, with the defaults of Table 1 of the paper: when the problem changes,
 * half of the population is removed according to its contribution to the hypervolume and replaced
 * by random solutions; when the reference points change, the whole population is replaced by random
 * solutions. A round has 250 iterations by default (25,000 evaluations with a population of 100),
 * every round is published, and the run only ends with {@link InDM2#requestStop()}.
 *
 * @param <S> the type of the solutions
 */
public class InDM2Builder<S extends Solution<?>> {
  public static final int DEFAULT_ITERATIONS_PER_ROUND = 250;

  private final InteractiveStepwiseAlgorithm<S> algorithm;
  private final DynamicProblem<S, Integer> problem;
  private RestartStrategy<S> restartStrategyForProblemChange;
  private RestartStrategy<S> restartStrategyForReferencePointChange;
  private int iterationsPerRound = DEFAULT_ITERATIONS_PER_ROUND;
  private DynamicStoppingCondition stoppingCondition = DynamicStoppingCondition.never();
  private QualityIndicator frontChangeIndicator = new InvertedGenerationalDistance();
  private double frontChangeThreshold = 0.0;

  /**
   * Creates the builder.
   *
   * @param algorithm the reference point-based algorithm
   * @param problem the dynamic problem
   * @param populationSize the population size of the algorithm, used by the default restart
   *     strategies
   */
  public InDM2Builder(
      InteractiveStepwiseAlgorithm<S> algorithm,
      DynamicProblem<S, Integer> problem,
      int populationSize) {
    Check.notNull(algorithm);
    Check.notNull(problem);
    Check.that(
        populationSize >= 2, () -> "The population size must be at least 2: " + populationSize);
    this.algorithm = algorithm;
    this.problem = problem;
    this.restartStrategyForProblemChange =
        new DefaultRestartStrategy<>(
            new RemoveNSolutionsAccordingToTheHypervolumeContribution<>(populationSize / 2),
            new CreateNRandomSolutions<>());
    this.restartStrategyForReferencePointChange =
        new DefaultRestartStrategy<>(
            new RemoveFirstNSolutions<>(populationSize), new CreateNRandomSolutions<>());
  }

  public InDM2Builder<S> setRestartStrategyForProblemChange(RestartStrategy<S> restartStrategy) {
    Check.notNull(restartStrategy);
    this.restartStrategyForProblemChange = restartStrategy;
    return this;
  }

  public InDM2Builder<S> setRestartStrategyForReferencePointChange(
      RestartStrategy<S> restartStrategy) {
    Check.notNull(restartStrategy);
    this.restartStrategyForReferencePointChange = restartStrategy;
    return this;
  }

  public InDM2Builder<S> setIterationsPerRound(int iterationsPerRound) {
    this.iterationsPerRound = iterationsPerRound;
    return this;
  }

  public InDM2Builder<S> setStoppingCondition(DynamicStoppingCondition stoppingCondition) {
    Check.notNull(stoppingCondition);
    this.stoppingCondition = stoppingCondition;
    return this;
  }

  /**
   * A round is published only if the indicator, with the last front published as reference, is
   * greater than the threshold (the threshold r of the paper, with IGD).
   */
  public InDM2Builder<S> setFrontChangeThreshold(QualityIndicator indicator, double threshold) {
    Check.notNull(indicator);
    this.frontChangeIndicator = indicator;
    this.frontChangeThreshold = threshold;
    return this;
  }

  public InDM2<S> build() {
    InDM2<S> indm2 =
        new InDM2<>(
            algorithm,
            problem,
            restartStrategyForProblemChange,
            restartStrategyForReferencePointChange,
            iterationsPerRound);
    indm2.stoppingCondition(stoppingCondition);
    indm2.frontChangeThreshold(frontChangeIndicator, frontChangeThreshold);
    return indm2;
  }
}
