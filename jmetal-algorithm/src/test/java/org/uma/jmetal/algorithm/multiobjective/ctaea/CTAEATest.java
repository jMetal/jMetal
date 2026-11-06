package org.uma.jmetal.algorithm.multiobjective.ctaea;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.Srinivas;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Unit tests for class CTAEA")
class CTAEATest {
  private static final int POPULATION_SIZE = 10;

  private Srinivas problem;
  private CTAEABuilder<DoubleSolution> builder;

  @BeforeEach
  void setUp() {
    JMetalRandom.getInstance().setSeed(12);
    problem = new Srinivas();
    builder =
        new CTAEABuilder<>(
                problem,
                new SBXCrossover(1.0, 20.0),
                new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0))
            .setPopulationSize(POPULATION_SIZE);
  }

  @Nested
  @DisplayName("When running the algorithm")
  class WhenRunningTheAlgorithm {

    @Test
    @DisplayName("given a budget, when running, then both archives have N solutions")
    void givenABudget_whenRunning_thenBothArchivesHaveNSolutions() {
      // Arrange
      CTAEA<DoubleSolution> algorithm = builder.setMaxEvaluations(500).build();

      // Act
      algorithm.run();

      // Assert
      assertEquals(POPULATION_SIZE, algorithm.result().size());
      assertEquals(POPULATION_SIZE, algorithm.diversityArchive().size());
      assertEquals(500, algorithm.evaluations());
    }

    @Test
    @DisplayName("given a budget not multiple of N, when running, then the last generation is full")
    void givenABudgetNotMultipleOfN_whenRunning_thenTheLastGenerationIsComplete() {
      // Arrange: 10 initial evaluations and 10 per generation, as in PlatEMO
      CTAEA<DoubleSolution> algorithm = builder.setMaxEvaluations(105).build();

      // Act
      algorithm.run();

      // Assert
      assertEquals(110, algorithm.evaluations());
    }

    @Test
    @DisplayName("given a budget of N, when running, then only the initial population is evaluated")
    void givenABudgetOfN_whenRunning_thenOnlyTheInitialPopulationIsEvaluated() {
      // Arrange
      CTAEA<DoubleSolution> algorithm = builder.setMaxEvaluations(POPULATION_SIZE).build();

      // Act
      algorithm.run();

      // Assert
      assertEquals(POPULATION_SIZE, algorithm.evaluations());
      assertEquals(POPULATION_SIZE, algorithm.result().size());
    }

    @Test
    @DisplayName("given a run, when reading the result, then it is a copy of the CA")
    void givenARun_whenReadingTheResult_thenItIsACopyOfTheConvergenceArchive() {
      // Arrange
      CTAEA<DoubleSolution> algorithm = builder.setMaxEvaluations(200).build();
      algorithm.run();

      // Act
      List<DoubleSolution> first = algorithm.result();
      first.clear();
      List<DoubleSolution> second = algorithm.result();

      // Assert
      assertNotSame(first, second);
      assertFalse(second.isEmpty());
    }
  }

  @Nested
  @DisplayName("When the algorithm has not run")
  class WhenTheAlgorithmHasNotRun {

    @Test
    @DisplayName("given a new algorithm, when reading the result, then it is empty")
    void givenANewAlgorithm_whenReadingTheResult_thenItIsEmpty() {
      // Arrange
      CTAEA<DoubleSolution> algorithm = builder.build();

      // Act & Assert
      assertTrue(algorithm.result().isEmpty());
      assertTrue(algorithm.diversityArchive().isEmpty());
      assertEquals(0, algorithm.evaluations());
    }
  }
}
