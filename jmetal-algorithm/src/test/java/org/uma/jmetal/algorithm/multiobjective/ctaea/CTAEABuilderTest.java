package org.uma.jmetal.algorithm.multiobjective.ctaea;

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
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.Srinivas;
import org.uma.jmetal.problem.multiobjective.cdtlz.C1_DTLZ1;
import org.uma.jmetal.problem.singleobjective.Sphere;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

@DisplayName("Unit tests for class CTAEABuilder")
class CTAEABuilderTest {
  private DoubleProblem problem;
  private SBXCrossover crossover;
  private PolynomialMutation mutation;
  private CTAEABuilder<DoubleSolution> builder;

  @BeforeEach
  void setUp() {
    problem = new C1_DTLZ1();
    crossover = new SBXCrossover(1.0, 20.0);
    mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);
    builder = new CTAEABuilder<>(problem, crossover, mutation);
  }

  @Nested
  @DisplayName("When using the default values")
  class WhenUsingTheDefaultValues {

    @Test
    @DisplayName("given a new builder, when reading its values, then they are the defaults")
    void givenANewBuilder_whenReadingItsValues_thenTheyAreTheDefaults() {
      // Act & Assert
      assertSame(problem, builder.getProblem());
      assertSame(crossover, builder.getCrossoverOperator());
      assertSame(mutation, builder.getMutationOperator());
      assertEquals(100, builder.getPopulationSize());
      assertEquals(25000, builder.getMaxEvaluations());
      assertInstanceOf(SequentialSolutionListEvaluator.class, builder.getSolutionListEvaluator());
    }

    @Test
    @DisplayName("given three objectives, when building, then the population size is 91")
    void givenThreeObjectives_whenBuilding_thenThePopulationSizeIs91() {
      // Act
      CTAEA<DoubleSolution> algorithm = builder.build();

      // Assert
      assertEquals(91, algorithm.populationSize());
      assertEquals(91, algorithm.weightVectors().length);
      assertEquals("C-TAEA", algorithm.name());
    }

    @Test
    @DisplayName("given two objectives, when building, then the population size is the requested")
    void givenTwoObjectives_whenBuilding_thenThePopulationSizeIsTheRequestedOne() {
      // Arrange
      Srinivas srinivas = new Srinivas();

      // Act
      CTAEA<DoubleSolution> algorithm =
          new CTAEABuilder<>(srinivas, crossover, mutation).setPopulationSize(60).build();

      // Assert
      assertEquals(60, algorithm.populationSize());
    }
  }

  @Nested
  @DisplayName("When setting the values")
  class WhenSettingTheValues {

    @Test
    @DisplayName("given valid values, when setting them, then the builder keeps them")
    void givenValidValues_whenSettingThem_thenTheBuilderKeepsThem() {
      // Arrange
      @SuppressWarnings("unchecked")
      SolutionListEvaluator<DoubleSolution> evaluator = mock(SolutionListEvaluator.class);

      // Act
      builder.setPopulationSize(50).setMaxEvaluations(1000).setSolutionListEvaluator(evaluator);

      // Assert
      assertEquals(50, builder.getPopulationSize());
      assertEquals(1000, builder.getMaxEvaluations());
      assertSame(evaluator, builder.getSolutionListEvaluator());
    }

    @Test
    @DisplayName("given a population size of zero, when setting it, then an exception is thrown")
    void givenAPopulationSizeOfZero_whenSettingIt_thenAnExceptionIsThrown() {
      // Act & Assert
      assertThrows(InvalidConditionException.class, () -> builder.setPopulationSize(0));
    }

    @Test
    @DisplayName("given zero evaluations, when setting them, then an exception is thrown")
    void givenZeroEvaluations_whenSettingThem_thenAnExceptionIsThrown() {
      // Act & Assert
      assertThrows(InvalidConditionException.class, () -> builder.setMaxEvaluations(0));
    }

    @Test
    @DisplayName("given a null evaluator, when setting it, then an exception is thrown")
    void givenANullEvaluator_whenSettingIt_thenAnExceptionIsThrown() {
      // Act & Assert
      assertThrows(NullParameterException.class, () -> builder.setSolutionListEvaluator(null));
    }
  }

  @Nested
  @DisplayName("When creating the builder with invalid arguments")
  class WhenCreatingTheBuilderWithInvalidArguments {

    @Test
    @DisplayName("given a null argument, when creating the builder, then an exception is thrown")
    void givenANullArgument_whenCreatingTheBuilder_thenAnExceptionIsThrown() {
      // Act & Assert
      assertThrows(
          NullParameterException.class, () -> new CTAEABuilder<>(null, crossover, mutation));
      assertThrows(
          NullParameterException.class, () -> new CTAEABuilder<>(problem, null, mutation));
      assertThrows(
          NullParameterException.class, () -> new CTAEABuilder<>(problem, crossover, null));
    }

    @Test
    @DisplayName("given one objective, when creating the builder, then an exception is thrown")
    void givenOneObjective_whenCreatingTheBuilder_thenAnExceptionIsThrown() {
      // Arrange
      Sphere sphere = new Sphere(10);

      // Act & Assert
      assertThrows(
          InvalidConditionException.class, () -> new CTAEABuilder<>(sphere, crossover, mutation));
    }

    @Test
    @DisplayName("given a crossover of three parents, when creating, then an exception is thrown")
    void givenACrossoverOfThreeParents_whenCreatingTheBuilder_thenAnExceptionIsThrown() {
      // Arrange
      @SuppressWarnings("unchecked")
      CrossoverOperator<DoubleSolution> threeParentCrossover = mock(CrossoverOperator.class);
      when(threeParentCrossover.numberOfRequiredParents()).thenReturn(3);

      // Act & Assert
      assertThrows(
          InvalidConditionException.class,
          () -> new CTAEABuilder<>(problem, threeParentCrossover, mutation));
    }
  }
}
