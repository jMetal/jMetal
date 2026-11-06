package org.uma.jmetal.algorithm.multiobjective.top;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidNumberException;
import org.uma.jmetal.util.errorchecking.exception.InvalidProbabilityValueException;
import org.uma.jmetal.util.errorchecking.exception.NonPositiveValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

@DisplayName("Unit tests for class ToPBuilder")
class ToPBuilderTest {
  private FakeDoubleProblem problem;
  private ToPBuilder builder;

  @BeforeEach
  void setUp() {
    problem = new FakeDoubleProblem(4, 2, 1);
    builder = new ToPBuilder(problem);
  }

  @Nested
  @DisplayName("When created")
  class WhenCreated {
    @Test
    @DisplayName("given a problem, when created, then the parameters are those of PlatEMO")
    void givenAProblem_whenCreated_thenTheParametersAreThoseOfPlatEMO() {
      // Act and Assert
      assertSame(problem, builder.getProblem());
      assertEquals(100, builder.getPopulationSize());
      assertEquals(25000, builder.getMaxEvaluations());
      assertEquals(1.0, builder.getCr());
      assertEquals(0.5, builder.getF());
      assertInstanceOf(SequentialSolutionListEvaluator.class, builder.getSolutionListEvaluator());
    }

    @Test
    @DisplayName("given a problem, when created, then the mutation is polynomial with p = 1/n")
    void givenAProblem_whenCreated_thenTheMutationIsPolynomialWithProbabilityOneOverN() {
      // Act
      MutationOperator<DoubleSolution> mutation = builder.getMutationOperator();

      // Assert
      PolynomialMutation polynomialMutation =
          assertInstanceOf(PolynomialMutation.class, mutation);
      assertEquals(0.25, polynomialMutation.mutationProbability());
      assertEquals(20.0, polynomialMutation.getDistributionIndex());
    }

    @Test
    @DisplayName("given a null problem, when created, then an exception is thrown")
    void givenANullProblem_whenCreated_thenAnExceptionIsThrown() {
      // Act and Assert
      assertThrows(NullParameterException.class, () -> new ToPBuilder(null));
    }
  }

  @Nested
  @DisplayName("When setting the parameters")
  class WhenSettingTheParameters {
    @Test
    @DisplayName("given valid values, when setting them, then the builder returns them")
    void givenValidValues_whenSettingThem_thenTheBuilderReturnsThem() {
      // Arrange
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(0.1, 15.0);
      SolutionListEvaluator<DoubleSolution> evaluator = new SequentialSolutionListEvaluator<>();

      // Act
      ToPBuilder result =
          builder
              .setPopulationSize(50)
              .setMaxEvaluations(1000)
              .setCr(0.9)
              .setF(0.8)
              .setMutationOperator(mutation)
              .setSolutionListEvaluator(evaluator);

      // Assert
      assertSame(builder, result);
      assertEquals(50, builder.getPopulationSize());
      assertEquals(1000, builder.getMaxEvaluations());
      assertEquals(0.9, builder.getCr());
      assertEquals(0.8, builder.getF());
      assertSame(mutation, builder.getMutationOperator());
      assertSame(evaluator, builder.getSolutionListEvaluator());
    }

    @Test
    @DisplayName("given the builder, when building, then the algorithm is ToP")
    void givenTheBuilder_whenBuilding_thenTheAlgorithmIsToP() {
      // Act
      ToP algorithm = builder.build();

      // Assert
      assertEquals("ToP", algorithm.name());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    @DisplayName("given a non-positive population size, when setting it, then an exception")
    void givenANonPositivePopulationSize_whenSettingIt_thenAnExceptionIsThrown(int size) {
      // Act and Assert
      assertThrows(NonPositiveValueException.class, () -> builder.setPopulationSize(size));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -100})
    @DisplayName("given a non-positive maximum of evaluations, when setting it, then an exception")
    void givenANonPositiveMaximumOfEvaluations_whenSettingIt_thenAnExceptionIsThrown(int max) {
      // Act and Assert
      assertThrows(NonPositiveValueException.class, () -> builder.setMaxEvaluations(max));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.1, Double.NaN})
    @DisplayName("given a CR out of [0, 1], when setting it, then an exception is thrown")
    void givenACrOutOfTheUnitInterval_whenSettingIt_thenAnExceptionIsThrown(double cr) {
      // Act and Assert
      assertThrows(InvalidProbabilityValueException.class, () -> builder.setCr(cr));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -0.5, Double.NaN})
    @DisplayName("given a non-positive F, when setting it, then an exception is thrown")
    void givenANonPositiveF_whenSettingIt_thenAnExceptionIsThrown(double f) {
      // Act and Assert
      assertThrows(NonPositiveValueException.class, () -> builder.setF(f));
    }

    @Test
    @DisplayName("given an infinite F, when setting it, then an exception is thrown")
    void givenAnInfiniteF_whenSettingIt_thenAnExceptionIsThrown() {
      // Act and Assert
      assertThrows(
          InvalidNumberException.class, () -> builder.setF(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("given null operators, when setting them, then an exception is thrown")
    void givenNullOperators_whenSettingThem_thenAnExceptionIsThrown() {
      // Act and Assert
      assertThrows(NullParameterException.class, () -> builder.setMutationOperator(null));
      assertThrows(NullParameterException.class, () -> builder.setSolutionListEvaluator(null));
    }
  }
}
