package org.uma.jmetal.algorithm.multiobjective.ccmo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.algorithm.multiobjective.ccmo.CCMO.VariationType;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover.DE_VARIANT;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NonPositiveValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

@DisplayName("Unit tests for class CCMOBuilder")
class CCMOBuilderTest {
  private Problem<DoubleSolution> problem;
  private CrossoverOperator<DoubleSolution> crossover;
  private MutationOperator<DoubleSolution> mutation;
  private CCMOBuilder<DoubleSolution> builder;

  @SuppressWarnings("unchecked")
  @BeforeEach
  void setUp() {
    problem = mock(Problem.class);
    crossover = new SBXCrossover(1.0, 20.0);
    mutation = new PolynomialMutation(0.1, 20.0);
    builder = new CCMOBuilder<>(problem, crossover, mutation);
  }

  @Nested
  @DisplayName("When created")
  class WhenCreated {

    @Test
    @DisplayName("given a new builder, when reading it, then it has the default configuration")
    void givenANewBuilder_whenReadingIt_thenItHasTheDefaultConfiguration() {
      // Assert
      assertSame(problem, builder.getProblem());
      assertSame(crossover, builder.getCrossoverOperator());
      assertSame(mutation, builder.getMutationOperator());
      assertEquals(100, builder.getPopulationSize());
      assertEquals(25000, builder.getMaxEvaluations());
      assertEquals(VariationType.GA, builder.getVariationType());
      assertInstanceOf(SequentialSolutionListEvaluator.class, builder.getSolutionListEvaluator());
    }

    @Test
    @DisplayName("given a null argument, when creating a builder, then an exception is thrown")
    void givenANullArgument_whenCreatingABuilder_thenAnExceptionIsThrown() {
      // Act and Assert
      assertThrows(
          NullParameterException.class, () -> new CCMOBuilder<>(null, crossover, mutation));
      assertThrows(NullParameterException.class, () -> new CCMOBuilder<>(problem, null, mutation));
      assertThrows(
          NullParameterException.class, () -> new CCMOBuilder<>(problem, crossover, null));
    }
  }

  @Nested
  @DisplayName("When setting the parameters")
  class WhenSettingTheParameters {

    @Test
    @DisplayName("given valid values, when setting them, then the builder keeps them")
    void givenValidValues_whenSettingThem_thenTheBuilderKeepsThem() {
      // Arrange
      @SuppressWarnings("unchecked")
      SolutionListEvaluator<DoubleSolution> evaluator = mock(SolutionListEvaluator.class);

      // Act
      builder
          .setPopulationSize(50)
          .setMaxEvaluations(1000)
          .setVariationType(VariationType.DE)
          .setSolutionListEvaluator(evaluator);

      // Assert
      assertEquals(50, builder.getPopulationSize());
      assertEquals(1000, builder.getMaxEvaluations());
      assertEquals(VariationType.DE, builder.getVariationType());
      assertSame(evaluator, builder.getSolutionListEvaluator());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1})
    @DisplayName(
        "given a population size lower than 2, when setting it, then an exception is thrown")
    void givenAPopulationSizeLowerThanTwo_whenSettingIt_thenAnExceptionIsThrown(int size) {
      // Act and Assert
      assertThrows(InvalidConditionException.class, () -> builder.setPopulationSize(size));
    }

    @ParameterizedTest
    @ValueSource(ints = {-100, 0})
    @DisplayName(
        "given a non-positive number of evaluations, when setting it, then an exception is thrown")
    void givenANonPositiveNumberOfEvaluations_whenSettingIt_thenAnExceptionIsThrown(int value) {
      // Act and Assert
      assertThrows(NonPositiveValueException.class, () -> builder.setMaxEvaluations(value));
    }

    @Test
    @DisplayName("given null values, when setting them, then an exception is thrown")
    void givenNullValues_whenSettingThem_thenAnExceptionIsThrown() {
      // Act and Assert
      assertThrows(NullParameterException.class, () -> builder.setVariationType(null));
      assertThrows(NullParameterException.class, () -> builder.setSolutionListEvaluator(null));
    }
  }

  @Nested
  @DisplayName("When building")
  class WhenBuilding {

    @Test
    @DisplayName("given the default configuration, when building, then CCMO uses the GA variation")
    void givenTheDefaultConfiguration_whenBuilding_thenCcmoUsesTheGaVariation() {
      // Act
      CCMO<DoubleSolution> algorithm = builder.build();

      // Assert
      assertEquals(VariationType.GA, algorithm.variationType());
      assertEquals("CCMO", algorithm.name());
      assertEquals(0, algorithm.result().size());
    }

    @Test
    @DisplayName(
        "given the DE variation and a crossover of the GA, when building, then an exception is"
            + " thrown")
    void givenTheDeVariationAndAGaCrossover_whenBuilding_thenAnExceptionIsThrown() {
      // Arrange
      builder.setVariationType(VariationType.DE);

      // Act and Assert
      assertThrows(InvalidConditionException.class, () -> builder.build());
    }

    @ParameterizedTest
    @EnumSource(
        value = DE_VARIANT.class,
        names = {"RAND_1_BIN", "RAND_1_EXP"})
    @DisplayName(
        "given the DE variation and a DE crossover with a random base vector, when building, then"
            + " CCMO uses the DE variation")
    void givenTheDeVariationAndADeCrossover_whenBuilding_thenCcmoUsesTheDeVariation(
        DE_VARIANT variant) {
      // Arrange
      var deCrossover = new DifferentialEvolutionCrossover(1.0, 0.5, variant);
      var deBuilder =
          new CCMOBuilder<>(problem, deCrossover, mutation).setVariationType(VariationType.DE);

      // Act
      CCMO<DoubleSolution> algorithm = deBuilder.build();

      // Assert
      assertEquals(VariationType.DE, algorithm.variationType());
    }

    @ParameterizedTest
    @EnumSource(
        value = DE_VARIANT.class,
        names = {"RAND_2_BIN", "BEST_1_BIN", "CURRENT_TO_RAND_1_BIN"})
    @DisplayName(
        "given the DE variation and another DE variant, when building, then an exception is thrown")
    void givenTheDeVariationAndAnotherDeVariant_whenBuilding_thenAnExceptionIsThrown(
        DE_VARIANT variant) {
      // Arrange
      var deCrossover = new DifferentialEvolutionCrossover(1.0, 0.5, variant);
      var deBuilder =
          new CCMOBuilder<>(problem, deCrossover, mutation).setVariationType(VariationType.DE);

      // Act and Assert
      assertThrows(InvalidConditionException.class, deBuilder::build);
    }

    @Test
    @DisplayName(
        "given the GA variation and a DE crossover, when building, then an exception is thrown")
    void givenTheGaVariationAndADeCrossover_whenBuilding_thenAnExceptionIsThrown() {
      // Arrange
      var gaBuilder =
          new CCMOBuilder<>(
              problem,
              new DifferentialEvolutionCrossover(1.0, 0.5, DE_VARIANT.RAND_1_BIN),
              mutation);

      // Act and Assert
      assertThrows(InvalidConditionException.class, gaBuilder::build);
    }
  }
}
