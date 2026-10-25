package org.uma.jmetal.algorithm.multiobjective.cmoeams;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover.DE_VARIANT;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.crossover.impl.UnimodalNormalDistributionCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.Srinivas;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NonPositiveValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

@DisplayName("Unit tests for class CMOEAMSBuilder")
class CMOEAMSBuilderTest {
  private Srinivas problem;
  private CrossoverOperator<DoubleSolution> crossover;
  private MutationOperator<DoubleSolution> mutation;
  private CMOEAMSBuilder<DoubleSolution> builder;

  @BeforeEach
  void setUp() {
    problem = new Srinivas();
    crossover = new SBXCrossover(1.0, 20.0);
    mutation = new PolynomialMutation(0.5, 20.0);
    builder = new CMOEAMSBuilder<>(problem, crossover, mutation);
  }

  @Nested
  @DisplayName("When creating the builder")
  class WhenCreatingTheBuilder {
    @Test
    @DisplayName("given the operators, then the defaults are those of the paper and PlatEMO")
    void givenTheOperators_whenCreating_thenTheDefaultsAreSet() {
      // Assert
      assertSame(problem, builder.getProblem());
      assertSame(crossover, builder.getCrossover());
      assertSame(mutation, builder.getMutation());
      assertEquals(100, builder.getPopulationSize());
      assertEquals(25000, builder.getMaxEvaluations());
      assertEquals(0.5, builder.getLambda());
      assertInstanceOf(SequentialSolutionListEvaluator.class, builder.getSolutionListEvaluator());
    }

    @Test
    @DisplayName("given a null problem or operator, then it is rejected")
    void givenANullProblemOrOperator_whenCreating_thenItIsRejected() {
      // Act & Assert
      assertThrows(
          NullParameterException.class, () -> new CMOEAMSBuilder<>(null, crossover, mutation));
      assertThrows(
          NullParameterException.class, () -> new CMOEAMSBuilder<>(problem, null, mutation));
      assertThrows(
          NullParameterException.class, () -> new CMOEAMSBuilder<>(problem, crossover, null));
    }

    @Test
    @DisplayName("given a crossover with three parents, then it is rejected")
    void givenACrossoverWithThreeParents_whenCreating_thenItIsRejected() {
      // Arrange
      var threeParents = new UnimodalNormalDistributionCrossover(1.0);

      // Act & Assert
      assertThrows(
          InvalidConditionException.class,
          () -> new CMOEAMSBuilder<>(problem, threeParents, mutation));
    }

    @Test
    @DisplayName("given a crossover with one child, then it is rejected")
    void givenACrossoverWithOneChild_whenCreating_thenItIsRejected() {
      // Arrange
      @SuppressWarnings("unchecked")
      CrossoverOperator<DoubleSolution> oneChild = mock(CrossoverOperator.class);
      when(oneChild.numberOfRequiredParents()).thenReturn(2);
      when(oneChild.numberOfGeneratedChildren()).thenReturn(1);

      // Act & Assert
      assertThrows(
          InvalidConditionException.class,
          () -> new CMOEAMSBuilder<>(problem, oneChild, mutation));
    }

    @ParameterizedTest(name = "variant {0}")
    @EnumSource(
        value = DE_VARIANT.class,
        names = {"RAND_1_BIN", "RAND_1_EXP"})
    @DisplayName("given a differential evolution with one random difference, then it is accepted")
    void givenADifferentialEvolutionWithOneRandomDifference_whenCreating_thenItIsAccepted(
        DE_VARIANT variant) {
      // Arrange
      var differentialEvolution = new DifferentialEvolutionCrossover(1.0, 0.5, variant);

      // Act & Assert
      assertDoesNotThrow(() -> new CMOEAMSBuilder<>(problem, differentialEvolution, mutation));
    }

    @ParameterizedTest(name = "variant {0}")
    @EnumSource(
        value = DE_VARIANT.class,
        names = {"RAND_2_BIN", "BEST_1_BIN", "RAND_TO_BEST_1_BIN", "CURRENT_TO_RAND_1_BIN"})
    @DisplayName("given another differential evolution, then it is rejected")
    void givenAnotherDifferentialEvolution_whenCreating_thenItIsRejected(DE_VARIANT variant) {
      // Arrange
      var differentialEvolution = new DifferentialEvolutionCrossover(1.0, 0.5, variant);

      // Act & Assert
      assertThrows(
          InvalidConditionException.class,
          () -> new CMOEAMSBuilder<>(problem, differentialEvolution, mutation));
    }
  }

  @Nested
  @DisplayName("When setting the parameters")
  class WhenSettingTheParameters {
    @Test
    @DisplayName("given valid values, then they are passed to the algorithm")
    void givenValidValues_whenBuilding_thenTheyAreUsed() {
      // Arrange
      SolutionListEvaluator<DoubleSolution> evaluator = new SequentialSolutionListEvaluator<>();

      // Act
      CMOEAMS<DoubleSolution> algorithm =
          builder
              .setPopulationSize(4)
              .setMaxEvaluations(12)
              .setLambda(0.0)
              .setSolutionListEvaluator(evaluator)
              .build();
      algorithm.run();

      // Assert
      assertEquals(4, builder.getPopulationSize());
      assertEquals(12, builder.getMaxEvaluations());
      assertEquals(0.0, builder.getLambda());
      assertSame(evaluator, builder.getSolutionListEvaluator());
      assertEquals(4, algorithm.result().size());
      assertEquals(12, algorithm.evaluations());
    }

    @ParameterizedTest(name = "population size {0}")
    @ValueSource(ints = {-1, 0, 1})
    @DisplayName("given a population size lower than 2, then it is rejected")
    void givenAPopulationSizeLowerThanTwo_whenSetting_thenItIsRejected(int populationSize) {
      // Act & Assert
      assertThrows(
          InvalidConditionException.class, () -> builder.setPopulationSize(populationSize));
    }

    @ParameterizedTest(name = "maximum number of evaluations {0}")
    @ValueSource(ints = {-1, 0})
    @DisplayName("given a maximum number of evaluations that is not positive, then it is rejected")
    void givenAMaximumNumberOfEvaluationsThatIsNotPositive_whenSetting_thenItIsRejected(
        int maxEvaluations) {
      // Act & Assert
      assertThrows(
          NonPositiveValueException.class, () -> builder.setMaxEvaluations(maxEvaluations));
    }

    @ParameterizedTest(name = "lambda {0}")
    @ValueSource(doubles = {-0.1, 1.1, Double.NaN})
    @DisplayName("given a lambda out of [0, 1], then it is rejected")
    void givenALambdaOutOfTheUnitInterval_whenSetting_thenItIsRejected(double lambda) {
      // Act & Assert
      assertThrows(InvalidConditionException.class, () -> builder.setLambda(lambda));
    }

    @Test
    @DisplayName("given a null evaluator, then it is rejected")
    void givenANullEvaluator_whenSetting_thenItIsRejected() {
      // Act & Assert
      assertThrows(NullParameterException.class, () -> builder.setSolutionListEvaluator(null));
    }
  }
}
