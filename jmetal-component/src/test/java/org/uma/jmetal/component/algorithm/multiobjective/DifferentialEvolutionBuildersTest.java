package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.impl.NullMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("The builders of the algorithms based on differential evolution")
class DifferentialEvolutionBuildersTest {
  private static final int POPULATION_SIZE = 5;
  private static final int NUMBER_OF_VARIABLES = 10;

  private static final BiFunction<DoubleProblem, Integer, EvolutionaryAlgorithm<DoubleSolution>>
      NSGAIIDE =
          (problem, evaluations) ->
              new NSGAIIDEBuilder(
                      problem,
                      POPULATION_SIZE,
                      0.0,
                      0.5,
                      new NullMutation<>(),
                      DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN)
                  .setTermination(new TerminationByEvaluations(evaluations))
                  .build();

  private static final BiFunction<DoubleProblem, Integer, EvolutionaryAlgorithm<DoubleSolution>>
      SMSEMOADE =
          (problem, evaluations) ->
              new SMSEMOADEBuilder(
                      problem,
                      POPULATION_SIZE,
                      0.0,
                      0.5,
                      new NullMutation<>(),
                      DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN)
                  .setTermination(new TerminationByEvaluations(evaluations))
                  .build();

  private static int sameVariables(DoubleSolution first, DoubleSolution second) {
    return (int)
        IntStream.range(0, first.variables().size())
            .filter(i -> first.variables().get(i).equals(second.variables().get(i)))
            .count();
  }

  /**
   * With a crossover probability of zero, the offspring is the solution to which differential
   * evolution is applied, except for the variable that is always crossed. Running the steps of the
   * algorithm by hand shows which solution that is.
   */
  private static void assertTheSolutionOfTheStepIsTheCurrentOne(
      BiFunction<DoubleProblem, Integer, EvolutionaryAlgorithm<DoubleSolution>> factory) {
    DoubleProblem problem = new ZDT1(NUMBER_OF_VARIABLES);
    EvolutionaryAlgorithm<DoubleSolution> algorithm = factory.apply(problem, 1000);

    List<DoubleSolution> population = algorithm.createInitialPopulation().create();
    population = algorithm.evaluation().evaluate(population);

    for (int step = 0; step < 2 * POPULATION_SIZE; step++) {
      DoubleSolution expected = population.get(step % POPULATION_SIZE);
      List<DoubleSolution> matingPool = algorithm.selection().select(population);
      List<DoubleSolution> offspring = algorithm.variation().variate(population, matingPool);

      assertThat(sameVariables(offspring.get(0), expected))
          .as("step %d", step)
          .isGreaterThanOrEqualTo(NUMBER_OF_VARIABLES - 1);

      offspring = algorithm.evaluation().evaluate(offspring);
      population = algorithm.replacement().replace(population, offspring);
    }
  }

  @ParameterizedTest(name = "{0}")
  @ValueSource(strings = {"NSGA-II-DE", "SMS-EMOA-DE"})
  @DisplayName("given a step of the algorithm, then differential evolution is applied to the next solution of the population")
  void givenASteOfTheAlgorithm_thenDifferentialEvolutionIsAppliedToTheNextSolution(String name) {
    assertTheSolutionOfTheStepIsTheCurrentOne(name.equals("NSGA-II-DE") ? NSGAIIDE : SMSEMOADE);
  }

  @Test
  @DisplayName("given a NSGA-II-DE builder, when it builds two algorithms, then they do not share components")
  void givenANSGAIIDEBuilder_whenItBuildsTwoAlgorithms_thenTheyDoNotShareComponents() {
    var builder =
        new NSGAIIDEBuilder(
            new ZDT1(NUMBER_OF_VARIABLES),
            POPULATION_SIZE,
            0.5,
            0.5,
            new NullMutation<>(),
            DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    var first = builder.build();
    var second = builder.build();

    assertThat(first.selection()).isNotSameAs(second.selection());
    assertThat(first.variation()).isNotSameAs(second.variation());
    assertThat(first.replacement()).isNotSameAs(second.replacement());
  }

  @Test
  @DisplayName("given a SMS-EMOA-DE builder, when it builds two algorithms, then they do not share components")
  void givenASMSEMOADEBuilder_whenItBuildsTwoAlgorithms_thenTheyDoNotShareComponents() {
    var builder =
        new SMSEMOADEBuilder(
            new ZDT1(NUMBER_OF_VARIABLES),
            POPULATION_SIZE,
            0.5,
            0.5,
            new NullMutation<>(),
            DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    var first = builder.build();
    var second = builder.build();

    assertThat(first.selection()).isNotSameAs(second.selection());
    assertThat(first.variation()).isNotSameAs(second.variation());
    assertThat(first.replacement()).isNotSameAs(second.replacement());
  }
}
