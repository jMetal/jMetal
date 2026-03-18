package org.uma.jmetal.component.algorithm.multiobjective;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

@DisplayName("RVEABuilder tests")
class RVEABuilderTest {

  @Nested
  @DisplayName("Build method validation tests")
  class BuildMethodValidationTests {

    @Test
    @DisplayName("Given mismatched population size and reference vectors, when build is called, then throw an exception")
    void givenMismatchedPopulationSizeAndReferenceVectors_whenBuildIsCalled_thenThrowAnException() {
      // Arrange
      var problem = new DTLZ2(12, 3);
      var crossover = new SBXCrossover(0.9, 20.0);
      var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

      RVEABuilder<DoubleSolution> builder =
          new RVEABuilder<>(problem, 5, 1000, crossover, mutation, 2.0, 0.1, 2);

      // Act + Assert
      assertThrows(InvalidConditionException.class, builder::build);
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
  }
}
