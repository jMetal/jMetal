package org.uma.jmetal.algorithm.multiobjective.ccmo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.uma.jmetal.algorithm.multiobjective.ccmo.CCMO.VariationType;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover.DE_VARIANT;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Unit tests for class CCMO")
class CCMOTest {
  private BandProblem problem;

  /**
   * A problem with two variables, whose unconstrained Pareto front is {@code f2 = 1 - f1} with
   * {@code f1} in [0, 1], and whose constraint {@code x1 >= 0.5} makes half of that front
   * infeasible.
   */
  @SuppressWarnings("serial")
  private static class BandProblem extends AbstractDoubleProblem {
    BandProblem() {
      numberOfObjectives(2);
      numberOfConstraints(1);
      name("BandProblem");
      variableBounds(List.of(0.0, 0.0), List.of(1.0, 1.0));
    }

    @Override
    public DoubleSolution evaluate(DoubleSolution solution) {
      double x1 = solution.variables().get(0);
      double x2 = solution.variables().get(1);
      solution.objectives()[0] = x1;
      solution.objectives()[1] = 1.0 - x1 + x2;
      solution.constraints()[0] = x1 - 0.5;
      return solution;
    }
  }

  @BeforeEach
  void setUp() {
    JMetalRandom.getInstance().setSeed(1);
    problem = new BandProblem();
  }

  private CCMO<DoubleSolution> ccmo(int populationSize, int maxEvaluations, VariationType type) {
    CrossoverOperator<DoubleSolution> crossover =
        type == VariationType.GA
            ? new SBXCrossover(1.0, 20.0)
            : new DifferentialEvolutionCrossover(1.0, 0.5, DE_VARIANT.RAND_1_BIN);
    return new CCMOBuilder<>(problem, crossover, new PolynomialMutation(0.5, 20.0))
        .setPopulationSize(populationSize)
        .setMaxEvaluations(maxEvaluations)
        .setVariationType(type)
        .build();
  }

  @Nested
  @DisplayName("When counting the evaluations")
  class WhenCountingTheEvaluations {

    @ParameterizedTest
    @CsvSource({
      "10, 100, GA, 100",
      "10, 95, GA, 100",
      "11, 100, GA, 102",
      "10, 100, DE, 100",
      "10, 90, DE, 100",
      "10, 5, GA, 20"
    })
    @DisplayName(
        "given a budget, when running, then each population is evaluated once and each generation"
            + " evaluates N/2 (GA) or N (DE) offspring per population")
    void givenABudget_whenRunning_thenTheEvaluationsFollowTheVariation(
        int populationSize, int maxEvaluations, VariationType type, int expectedEvaluations) {
      // Arrange
      CCMO<DoubleSolution> algorithm = ccmo(populationSize, maxEvaluations, type);

      // Act
      algorithm.run();

      // Assert
      assertEquals(expectedEvaluations, algorithm.numberOfEvaluations());
      assertEquals(populationSize, algorithm.result().size());
      assertEquals(populationSize, algorithm.helperPopulation().size());
    }
  }

  @Nested
  @DisplayName("When solving a constrained problem")
  class WhenSolvingAConstrainedProblem {

    @ParameterizedTest
    @EnumSource(VariationType.class)
    @DisplayName(
        "given a constraint that cuts the front, when running, then the result is feasible and the"
            + " helper population ignores the constraint")
    void givenAConstraintThatCutsTheFront_whenRunning_thenOnlyTheResultIsFeasible(
        VariationType type) {
      // Arrange
      CCMO<DoubleSolution> algorithm = ccmo(20, 2000, type);

      // Act
      algorithm.run();

      // Assert
      assertTrue(algorithm.result().stream().allMatch(ConstraintHandling::isFeasible));
      assertTrue(
          algorithm.helperPopulation().stream().anyMatch(s -> !ConstraintHandling.isFeasible(s)));
    }
  }

  @Test
  @DisplayName("given a new instance, when reading its result, then it is empty")
  void givenANewInstance_whenReadingItsResult_thenItIsEmpty() {
    // Act
    CCMO<DoubleSolution> algorithm = ccmo(10, 100, VariationType.GA);

    // Assert
    assertTrue(algorithm.result().isEmpty());
    assertEquals(0, algorithm.numberOfEvaluations());
  }
}
