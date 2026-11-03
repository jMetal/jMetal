package org.uma.jmetal.component.algorithm.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Unit tests for class SMSEMOABuilder")
class SMSEMOABuilderTest {
  private static final int POPULATION_SIZE = 10;
  private static final int MAX_EVALUATIONS = 60;

  private ZDT1 problem;
  private SMSEMOABuilder<DoubleSolution> builder;

  @BeforeEach
  void setUp() {
    JMetalRandom.getInstance().setSeed(1);
    problem = new ZDT1();
    builder =
        new SMSEMOABuilder<>(
                problem,
                POPULATION_SIZE,
                new SBXCrossover(0.9, 20.0),
                new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0))
            .setTermination(new TerminationByEvaluations(MAX_EVALUATIONS));
  }

  private List<DoubleSolution> solutions(int count) {
    return IntStream.range(0, count).mapToObj(i -> problem.createSolution()).toList();
  }

  @Nested
  @DisplayName("When setting the creation of the initial population")
  class WhenSettingTheInitialPopulation {
    @Test
    @DisplayName("given solutions of the population size, when created, then they are used")
    void givenSolutionsOfThePopulationSize_whenCreated_thenTheyAreUsed() {
      // Arrange
      List<DoubleSolution> seeds = solutions(POPULATION_SIZE);
      var algorithm = builder.setCreateInitialPopulation(() -> seeds).build();

      // Act
      List<DoubleSolution> initialPopulation = algorithm.createInitialPopulation().create();

      // Assert
      assertThat(initialPopulation).containsExactlyElementsOf(seeds);
    }

    @Test
    @DisplayName("given an unmodifiable list, when the algorithm runs, then it keeps the size")
    void givenAnUnmodifiableList_whenTheAlgorithmRuns_thenItKeepsThePopulationSize() {
      // Arrange
      List<DoubleSolution> seeds = solutions(POPULATION_SIZE);
      var algorithm = builder.setCreateInitialPopulation(() -> seeds).build();

      // Act
      algorithm.run();

      // Assert
      assertThat(algorithm.result()).hasSize(POPULATION_SIZE);
      assertThat(algorithm.numberOfEvaluations()).isEqualTo(MAX_EVALUATIONS);
    }

    @Test
    @DisplayName("given solutions of another size, when the algorithm runs, then it fails")
    void givenSolutionsOfAnotherSize_whenTheAlgorithmRuns_thenItFails() {
      // Arrange
      List<DoubleSolution> seeds = solutions(POPULATION_SIZE - 1);
      var algorithm = builder.setCreateInitialPopulation(() -> seeds).build();

      // Act & Assert
      assertThrows(InvalidConditionException.class, algorithm::run);
    }

    @Test
    @DisplayName("given a null creation, when it is set, then an exception is thrown")
    void givenANullCreation_whenItIsSet_thenAnExceptionIsThrown() {
      // Act & Assert
      assertThrows(NullParameterException.class, () -> builder.setCreateInitialPopulation(null));
    }
  }

  @Nested
  @DisplayName("When the initial population is not set")
  class WhenTheInitialPopulationIsNotSet {
    @Test
    @DisplayName("given the default creation, when created, then it has the population size")
    void givenTheDefaultCreation_whenCreated_thenItHasThePopulationSize() {
      // Arrange
      var algorithm = builder.build();

      // Act
      List<DoubleSolution> initialPopulation = algorithm.createInitialPopulation().create();

      // Assert
      assertThat(initialPopulation).hasSize(POPULATION_SIZE);
    }
  }
}
