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

@DisplayName("Unit tests for class GAA")
class GAATest {

  private DoubleProblem problem;

  @BeforeEach
  void setUp() {
    problem = new GAA();
  }

  @Nested
  @DisplayName("When checking its properties")
  class WhenCheckingProperties {

    @Test
    @DisplayName("given GAA, when checking number of variables, then it is 27")
    void givenGAA_whenCheckingNumberOfVariables_thenItIs27() {
      assertThat(problem.numberOfVariables()).isEqualTo(27);
    }

    @Test
    @DisplayName("given GAA, when checking number of objectives, then it is 10")
    void givenGAA_whenCheckingNumberOfObjectives_thenItIs10() {
      assertThat(problem.numberOfObjectives()).isEqualTo(10);
    }

    @Test
    @DisplayName("given GAA, when checking number of constraints, then it is 1")
    void givenGAA_whenCheckingNumberOfConstraints_thenItIs1() {
      assertThat(problem.numberOfConstraints()).isEqualTo(1);
    }

    @Test
    @DisplayName("given GAA, when checking name, then it is GAA")
    void givenGAA_whenCheckingName_thenItIsGAA() {
      assertThat(problem.name()).isEqualTo("GAA");
    }
  }

  @Nested
  @DisplayName("When checking its variable bounds")
  class WhenCheckingVariableBounds {

    @ParameterizedTest
    @DisplayName("given GAA, when checking lower bounds for design 1, then they are correct")
    @CsvSource({
        "0, 0.24",
        "1, 7.0",
        "2, 0.0",
        "3, 5.5",
        "4, 19.0",
        "5, 85.0",
        "6, 14.0",
        "7, 3.0",
        "8, 0.46"
    })
    void givenGAA_whenCheckingDesign1LowerBounds_thenTheyAreCorrect(int index, double expected) {
      assertThat(problem.variableBounds().get(index).getLowerBound()).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("given GAA, when checking upper bounds for design 1, then they are correct")
    @CsvSource({
        "0, 0.48",
        "1, 11.0",
        "2, 6.0",
        "3, 5.968",
        "4, 25.0",
        "5, 110.0",
        "6, 20.0",
        "7, 3.75",
        "8, 1.0"
    })
    void givenGAA_whenCheckingDesign1UpperBounds_thenTheyAreCorrect(int index, double expected) {
      assertThat(problem.variableBounds().get(index).getUpperBound()).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("given GAA, when checking that all three designs share the same bounds")
    @CsvSource({"0", "1", "2", "3", "4", "5", "6", "7", "8"})
    void givenGAA_whenCheckingBoundsAcrossDesigns_thenAllDesignsShareSameBounds(int offset) {
      double lb0 = problem.variableBounds().get(offset).getLowerBound();
      double lb1 = problem.variableBounds().get(9 + offset).getLowerBound();
      double lb2 = problem.variableBounds().get(18 + offset).getLowerBound();
      assertThat(lb0).isEqualTo(lb1).isEqualTo(lb2);

      double ub0 = problem.variableBounds().get(offset).getUpperBound();
      double ub1 = problem.variableBounds().get(9 + offset).getUpperBound();
      double ub2 = problem.variableBounds().get(18 + offset).getUpperBound();
      assertThat(ub0).isEqualTo(ub1).isEqualTo(ub2);
    }
  }

  @Nested
  @DisplayName("When evaluating a solution")
  class WhenEvaluatingASolution {

    @Test
    @DisplayName("given a default solution, when evaluated, then objectives and constraints have correct dimensions")
    void givenADefaultSolution_whenEvaluated_thenObjectivesAndConstraintsHaveCorrectDimensions() {
      DoubleSolution solution = problem.createSolution();
      problem.evaluate(solution);

      assertThat(solution.objectives()).hasSize(10);
      assertThat(solution.constraints()).hasSize(1);
    }

    @Test
    @DisplayName("given an identical solution across all three designs, when evaluated, then PFPF is zero")
    void givenIdenticalDesigns_whenEvaluated_thenPfpfIsZero() {
      DoubleSolution solution = problem.createSolution();
      // Set all 27 variables to the center of their bounds so all three designs are identical
      for (int d = 0; d < 3; d++) {
        for (int i = 0; i < 9; i++) {
          double lb = problem.variableBounds().get(i).getLowerBound();
          double ub = problem.variableBounds().get(i).getUpperBound();
          solution.variables().set(d * 9 + i, (lb + ub) / 2.0);
        }
      }
      problem.evaluate(solution);

      assertThat(solution.objectives()[9]).isEqualTo(0.0);
    }
  }
}
