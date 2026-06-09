package org.uma.jmetal.problem.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Unit tests for class CarSideImpact")
class CarSideImpactTest {

  private DoubleProblem problem;

  @BeforeEach
  void setUp() {
    problem = new CarSideImpact();
  }

  @Nested
  @DisplayName("When checking its properties")
  class WhenCheckingProperties {

    @Test
    @DisplayName("given CarSideImpact, when checking number of variables, then it is 7")
    void givenCarSideImpact_whenCheckingNumberOfVariables_thenItIsSeven() {
      assertThat(problem.numberOfVariables()).isEqualTo(7);
    }

    @Test
    @DisplayName("given CarSideImpact, when checking number of objectives, then it is 3")
    void givenCarSideImpact_whenCheckingNumberOfObjectives_thenItIsThree() {
      assertThat(problem.numberOfObjectives()).isEqualTo(3);
    }

    @Test
    @DisplayName("given CarSideImpact, when checking number of constraints, then it is 10")
    void givenCarSideImpact_whenCheckingNumberOfConstraints_thenItIsTen() {
      assertThat(problem.numberOfConstraints()).isEqualTo(10);
    }

    @Test
    @DisplayName("given CarSideImpact, when checking name, then it is CarSideImpact")
    void givenCarSideImpact_whenCheckingName_thenItIsCarSideImpact() {
      assertThat(problem.name()).isEqualTo("CarSideImpact");
    }
  }

  @Nested
  @DisplayName("When checking its variable bounds")
  class WhenCheckingVariableBounds {

    @ParameterizedTest
    @DisplayName("given CarSideImpact, when checking lower bounds, then they are correct")
    @CsvSource({
        "0, 0.5",
        "1, 0.45",
        "2, 0.5",
        "3, 0.5",
        "4, 0.875",
        "5, 0.4",
        "6, 0.4"
    })
    void givenCarSideImpact_whenCheckingLowerBounds_thenTheyAreCorrect(
        int index, double expected) {
      assertThat(problem.variableBounds().get(index).getLowerBound()).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("given CarSideImpact, when checking upper bounds, then they are correct")
    @CsvSource({
        "0, 1.5",
        "1, 1.35",
        "2, 1.5",
        "3, 1.5",
        "4, 2.625",
        "5, 1.2",
        "6, 1.2"
    })
    void givenCarSideImpact_whenCheckingUpperBounds_thenTheyAreCorrect(
        int index, double expected) {
      assertThat(problem.variableBounds().get(index).getUpperBound()).isEqualTo(expected);
    }
  }

  @Nested
  @DisplayName("When evaluating a solution")
  class WhenEvaluatingASolution {

    @Test
    @DisplayName("given a default solution, when evaluated, then objectives and constraints have correct dimensions")
    void givenADefaultSolution_whenEvaluated_thenObjectivesAndConstraintsHaveCorrectDimensions() {
      // Arrange
      DoubleSolution solution = problem.createSolution();

      // Act
      problem.evaluate(solution);

      // Assert
      assertThat(solution.objectives()).hasSize(3);
      assertThat(solution.constraints()).hasSize(10);
    }
  }
}
