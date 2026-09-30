package org.uma.jmetal.algorithm.singleobjective.coralreefsoptimization;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

@DisplayName("Unit tests for class CoralReefsOptimization")
class CoralReefsOptimizationTest {

  private CoralReefsOptimization<BinarySolution> algorithm;

  @BeforeEach
  void setUp() {
    OneMax problem = new OneMax(64);
    algorithm =
        new CoralReefsOptimizationBuilder<BinarySolution>(
                problem,
                new BinaryTournamentSelection<>(),
                new SinglePointCrossover(0.9),
                new BitFlipMutation(1.0 / 64))
            .setM(10)
            .setN(10)
            .setRho(0.6)
            .setFbs(0.9)
            .setFbr(0.1)
            .setFa(0.1)
            .setPd(0.1)
            .setAttemptsToSettle(3)
            .setComparator(new ObjectiveComparator<>(0))
            .setMaxEvaluations(50)
            .build();
  }

  @Nested
  @DisplayName("When running with a binary tournament")
  class WhenRunningWithABinaryTournament {

    @Test
    @DisplayName("given the settings of the runner, when running, then no exception is thrown")
    void givenTheSettingsOfTheRunner_whenRunning_thenNoExceptionIsThrown() {
      // Act & Assert
      assertDoesNotThrow(algorithm::run);
      assertFalse(algorithm.result().isEmpty());
    }
  }
}
