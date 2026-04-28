package org.uma.jmetal.component.algorithm.multiobjective;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.selection.impl.RandomSelection;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.NegativeValueException;
import org.uma.jmetal.util.errorchecking.exception.NonPositiveValueException;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.errorchecking.exception.ValueOutOfRangeException;
import org.uma.jmetal.util.referencepoint.ReferencePointGenerator;

@DisplayName("RVEABuilder tests")
class RVEABuilderTest {

  @Nested
  @DisplayName("Constructor validation tests")
  class ConstructorValidationTests {

    @Test
    @DisplayName("Given null problem, when builder is created, then throw NullParameterException")
    void givenNullProblem_whenBuilderIsCreated_thenThrowNullParameterException() {
      var crossover = new SBXCrossover(0.9, 20.0);
      var mutation = new PolynomialMutation(0.1, 20.0);

      assertThrows(NullParameterException.class,
          () -> new RVEABuilder<>(null, 6, 1000, crossover, mutation, 2.0, 0.1, 2));
    }

    @Test
    @DisplayName("Given null crossover, when builder is created, then throw NullParameterException")
    void givenNullCrossover_whenBuilderIsCreated_thenThrowNullParameterException() {
      var problem = new DTLZ2(12, 3);
      var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

      assertThrows(NullParameterException.class,
          () -> new RVEABuilder<>(problem, 6, 1000, null, mutation, 2.0, 0.1, 2));
    }

    @Test
    @DisplayName("Given null mutation, when builder is created, then throw NullParameterException")
    void givenNullMutation_whenBuilderIsCreated_thenThrowNullParameterException() {
      var problem = new DTLZ2(12, 3);
      var crossover = new SBXCrossover(0.9, 20.0);

      assertThrows(NullParameterException.class,
          () -> new RVEABuilder<>(problem, 6, 1000, crossover, null, 2.0, 0.1, 2));
    }

    @Test
    @DisplayName("Given non-positive population size, when builder is created, then throw NonPositiveValueException")
    void givenNonPositivePopulationSize_whenBuilderIsCreated_thenThrowNonPositiveValueException() {
      var problem = new DTLZ2(12, 3);
      var crossover = new SBXCrossover(0.9, 20.0);
      var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

      assertThrows(NonPositiveValueException.class,
          () -> new RVEABuilder<>(problem, 0, 1000, crossover, mutation, 2.0, 0.1, 2));
    }

    @Test
    @DisplayName("Given non-positive max evaluations, when builder is created, then throw NonPositiveValueException")
    void givenNonPositiveMaxEvaluations_whenBuilderIsCreated_thenThrowNonPositiveValueException() {
      var problem = new DTLZ2(12, 3);
      var crossover = new SBXCrossover(0.9, 20.0);
      var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

      assertThrows(NonPositiveValueException.class,
          () -> new RVEABuilder<>(problem, 6, 0, crossover, mutation, 2.0, 0.1, 2));
    }

    @Test
    @DisplayName("Given non-positive divisions, when builder is created, then throw NonPositiveValueException")
    void givenNonPositiveDivisions_whenBuilderIsCreated_thenThrowNonPositiveValueException() {
      var problem = new DTLZ2(12, 3);
      var crossover = new SBXCrossover(0.9, 20.0);
      var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

      assertThrows(NonPositiveValueException.class,
          () -> new RVEABuilder<>(problem, 6, 1000, crossover, mutation, 2.0, 0.1, 0));
    }

    @Test
    @DisplayName("Given a negative alpha, when builder is created, then throw NegativeValueException")
    void givenNegativeAlpha_whenBuilderIsCreated_thenThrowNegativeValueException() {
      var problem = new DTLZ2(12, 3);
      var crossover = new SBXCrossover(0.9, 20.0);
      var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

      assertThrows(NegativeValueException.class,
          () -> new RVEABuilder<>(problem, 6, 1000, crossover, mutation, -0.1, 0.1, 2));
    }

    @Test
    @DisplayName("Given an invalid frequency ratio, when builder is created, then throw ValueOutOfRangeException")
    void givenInvalidFrequencyRatio_whenBuilderIsCreated_thenThrowValueOutOfRangeException() {
      var problem = new DTLZ2(12, 3);
      var crossover = new SBXCrossover(0.9, 20.0);
      var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

      assertThrows(ValueOutOfRangeException.class,
          () -> new RVEABuilder<>(problem, 6, 1000, crossover, mutation, 2.0, 0.0, 2));
      assertThrows(ValueOutOfRangeException.class,
          () -> new RVEABuilder<>(problem, 6, 1000, crossover, mutation, 2.0, 1.1, 2));
    }
  }

  @Nested
  @DisplayName("Build method validation tests")
  class BuildMethodValidationTests {

    @Test
    @DisplayName("Given mismatched population size and generated reference vectors, when builder is created, then throw an exception")
    void givenMismatchedPopulationSizeAndGeneratedReferenceVectors_whenBuilderIsCreated_thenThrowAnException() {
      // Arrange
      var problem = new DTLZ2(12, 3);
      var crossover = new SBXCrossover(0.9, 20.0);
      var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

      // Act + Assert
      assertThrows(InvalidConditionException.class,
          () -> new RVEABuilder<>(problem, 5, 1000, crossover, mutation, 2.0, 0.1, 2));
    }

    @Test
    @DisplayName("Given a non evaluation termination, when build is called, then throw an exception")
    void givenANonEvaluationTermination_whenBuildIsCalled_thenThrowAnException() {
      // Arrange
      var problem = new DTLZ2(12, 3);
      var crossover = new SBXCrossover(0.9, 20.0);
      var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

      RVEABuilder<DoubleSolution> builder =
          new RVEABuilder<>(problem, 6, 1000, crossover, mutation, 2.0, 0.1, 2)
              .setTermination(mock(Termination.class));

      // Act + Assert
      assertThrows(InvalidConditionException.class, builder::build);
    }

    @Test
    @DisplayName("Given consistent settings, when build is called, then an algorithm instance is returned")
    void givenConsistentSettings_whenBuildIsCalled_thenAnAlgorithmInstanceIsReturned() {
      // Arrange
      var problem = new DTLZ2(12, 3);
      var crossover = new SBXCrossover(0.9, 20.0);
      var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

      RVEABuilder<DoubleSolution> builder =
          new RVEABuilder<>(problem, 6, 1000, crossover, mutation, 2.0, 0.1, 2)
              .setTermination(new TerminationByEvaluations(1000));

      // Act
      var algorithm = builder.build();

      // Assert
      assertNotNull(algorithm);
    }

    @Test
    @DisplayName("Given a custom variation and no custom selection, when build is called, then the default selection uses the final mating pool size")
    @SuppressWarnings("unchecked")
    void givenACustomVariationAndNoCustomSelection_whenBuildIsCalled_thenTheDefaultSelectionUsesTheFinalMatingPoolSize() {
      // Arrange
      var problem = new DTLZ2(12, 3);
      var crossover = new SBXCrossover(0.9, 20.0);
      var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);
      Variation<DoubleSolution> variation = mock(Variation.class);
      when(variation.matingPoolSize()).thenReturn(8);
      when(variation.offspringPopulationSize()).thenReturn(6);

      RVEABuilder<DoubleSolution> builder =
          new RVEABuilder<>(problem, 6, 1000, crossover, mutation, 2.0, 0.1, 2)
              .setVariation(variation);

      // Act
      var algorithm = builder.build();

      // Assert
      RandomSelection<DoubleSolution> selection =
          (RandomSelection<DoubleSolution>) algorithm.selection();
      assertEquals(8, selection.getNumberOfElementsToSelect());
    }

    @Test
    @DisplayName("Given a custom initial population with a wrong size, when the algorithm runs, then it fails fast")
    void givenACustomInitialPopulationWithAWrongSize_whenTheAlgorithmRuns_thenItFailsFast() {
      // Arrange
      var problem = new DTLZ2(12, 3);
      var crossover = new SBXCrossover(0.9, 20.0);
      var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

      RVEABuilder<DoubleSolution> builder =
          new RVEABuilder<>(problem, 6, 1000, crossover, mutation, 2.0, 0.1, 2)
              .setCreateInitialPopulation(() ->
                  IntStream.range(0, 5).mapToObj(i -> problem.createSolution()).toList());

      var algorithm = builder.build();

      // Act + Assert
      assertThrows(InvalidConditionException.class, algorithm::run);
    }

    @Test
    @DisplayName("Given custom reference vectors matching the population size, when build is called, then an algorithm instance is returned")
    void givenCustomReferenceVectorsMatchingThePopulationSize_whenBuildIsCalled_thenAnAlgorithmInstanceIsReturned() {
      // Arrange
      var problem = new DTLZ2(12, 3);
      var crossover = new SBXCrossover(0.9, 20.0);
      var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);
      List<double[]> referenceVectors =
          ReferencePointGenerator.generateTwoLayers(problem.numberOfObjectives(), 9, 8);

      RVEABuilder<DoubleSolution> builder =
          new RVEABuilder<>(problem, 100, 1000, crossover, mutation, 2.0, 0.1,
            referenceVectors);

      // Act
      var algorithm = builder.build();

      // Assert
      assertNotNull(algorithm);
    }

    @Test
    @DisplayName("Given max evaluations lower than population size, when build is called, then an algorithm instance is returned")
    void givenMaxEvaluationsLowerThanPopulationSize_whenBuildIsCalled_thenAnAlgorithmInstanceIsReturned() {
      // Arrange
      var problem = new DTLZ2(12, 3);
      var crossover = new SBXCrossover(0.9, 20.0);
      var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

      RVEABuilder<DoubleSolution> builder =
          new RVEABuilder<>(problem, 6, 5, crossover, mutation, 2.0, 0.1, 2)
              .setTermination(new TerminationByEvaluations(5));

      // Act
      var algorithm = builder.build();

      // Assert
      assertNotNull(algorithm);
    }
  }
}
