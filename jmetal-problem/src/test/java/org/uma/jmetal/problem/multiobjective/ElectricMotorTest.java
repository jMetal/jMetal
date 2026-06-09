package org.uma.jmetal.problem.multiobjective;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

@DisplayName("Unit tests for class ElectricMotor")
class ElectricMotorTest {

  private DoubleProblem problem;

  @BeforeEach
  void setUp() {
    problem = new ElectricMotor();
  }

  @Nested
  @DisplayName("When checking its properties")
  class WhenCheckingProperties {

    @Test
    @DisplayName("given ElectricMotor, when checking number of variables, then it is 80")
    void givenElectricMotor_whenCheckingNumberOfVariables_thenItIs80() {
      assertThat(problem.numberOfVariables()).isEqualTo(80);
    }

    @Test
    @DisplayName("given ElectricMotor, when checking number of objectives, then it is 20")
    void givenElectricMotor_whenCheckingNumberOfObjectives_thenItIs20() {
      assertThat(problem.numberOfObjectives()).isEqualTo(20);
    }

    @Test
    @DisplayName("given ElectricMotor, when checking number of constraints, then it is 60")
    void givenElectricMotor_whenCheckingNumberOfConstraints_thenItIs60() {
      assertThat(problem.numberOfConstraints()).isEqualTo(60);
    }

    @Test
    @DisplayName("given ElectricMotor, when checking name, then it is ElectricMotor")
    void givenElectricMotor_whenCheckingName_thenItIsElectricMotor() {
      assertThat(problem.name()).isEqualTo("ElectricMotor");
    }
  }

  @Nested
  @DisplayName("When checking its variable bounds")
  class WhenCheckingVariableBounds {

    @ParameterizedTest
    @DisplayName("given ElectricMotor, when checking lower bounds for motor 1, then they are correct")
    @CsvSource({
        "0, 100.0",
        "1, 0.01",
        "2, 1.0",
        "3, 0.01",
        "4, 0.01",
        "5, 0.0005",
        "6, 0.001",
        "7, 0.1"
    })
    void givenElectricMotor_whenCheckingMotor1LowerBounds_thenTheyAreCorrect(
        int index, double expected) {
      assertThat(problem.variableBounds().get(index).getLowerBound()).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("given ElectricMotor, when checking upper bounds for motor 1, then they are correct")
    @CsvSource({
        "0, 1500.0",
        "1, 1.0",
        "2, 500.0",
        "3, 1.0",
        "4, 0.1",
        "5, 0.1",
        "6, 0.1",
        "7, 6.0"
    })
    void givenElectricMotor_whenCheckingMotor1UpperBounds_thenTheyAreCorrect(
        int index, double expected) {
      assertThat(problem.variableBounds().get(index).getUpperBound()).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("given ElectricMotor, when checking that all ten motors share the same bounds")
    @CsvSource({"0", "1", "2", "3", "4", "5", "6", "7"})
    void givenElectricMotor_whenCheckingBoundsAcrossMotors_thenAllMotorsShareSameBounds(
        int offset) {
      double lb0 = problem.variableBounds().get(offset).getLowerBound();
      for (int m = 1; m < 10; m++) {
        assertThat(problem.variableBounds().get(8 * m + offset).getLowerBound()).isEqualTo(lb0);
      }
      double ub0 = problem.variableBounds().get(offset).getUpperBound();
      for (int m = 1; m < 10; m++) {
        assertThat(problem.variableBounds().get(8 * m + offset).getUpperBound()).isEqualTo(ub0);
      }
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

      assertThat(solution.objectives()).hasSize(20);
      assertThat(solution.constraints()).hasSize(60);
    }

    @Test
    @DisplayName("given upper-bound solution, when evaluated, then mass and efficiency match reference values")
    void givenUpperBoundSolution_whenEvaluated_thenMassAndEfficiencyMatchReferenceValues() {
      // Reference values taken from MOEAFramework ElectricMotorTest.testUpperBound
      DoubleSolution solution = problem.createSolution();
      for (int i = 0; i < 80; i++) {
        solution.variables().set(i, problem.variableBounds().get(i).getUpperBound());
      }
      problem.evaluate(solution);

      // All motors have the same upper bounds, so all (MASS, EFFIC) pairs are identical
      for (int m = 0; m < 10; m++) {
        assertThat(solution.objectives()[2 * m]).isCloseTo(0.08781367214553605, within(1e-9));
        assertThat(solution.objectives()[2 * m + 1]).isCloseTo(-0.9739676521739131, within(1e-9));
      }
    }

    @Test
    @DisplayName("given upper-bound solution, when evaluated, then FEAS constraint equals 10 for all motors")
    void givenUpperBoundSolution_whenEvaluated_thenFeasConstraintIs10ForAllMotors() {
      DoubleSolution solution = problem.createSolution();
      for (int i = 0; i < 80; i++) {
        solution.variables().set(i, problem.variableBounds().get(i).getUpperBound());
      }
      problem.evaluate(solution);

      // FEAS = RADIUS/THICK. At upper bounds: (0.1/100) / (0.1/1000) = 0.001/0.0001 = 10.
      // jMetal constraint: FEAS - 1 = 9
      for (int m = 0; m < 10; m++) {
        assertThat(solution.constraints()[6 * m + 2]).isCloseTo(9.0, within(1e-9));
      }
    }
  }

  @Nested
  @DisplayName("When evaluating a single motor")
  class WhenEvaluatingSingleMotor {

    @Test
    @DisplayName("given upper-bound inputs, when evaluateMotor is called, then FEAS equals 10")
    void givenUpperBoundInputs_whenEvaluateMotorCalled_thenFeasEquals10() {
      double[] input = {1500, 1.0, 500, 1.0, 0.1, 0.1, 0.1, 6.0};
      double[] output = ElectricMotor.evaluateMotor(input);

      assertThat(output[5]).isCloseTo(10.0, within(1e-9));  // FEAS
    }

    @Test
    @DisplayName("given upper-bound inputs, when evaluateMotor is called, then mass matches reference")
    void givenUpperBoundInputs_whenEvaluateMotorCalled_thenMassMatchesReference() {
      double[] input = {1500, 1.0, 500, 1.0, 0.1, 0.1, 0.1, 6.0};
      double[] output = ElectricMotor.evaluateMotor(input);

      assertThat(output[1]).isCloseTo(0.08781367214553605, within(1e-9));  // MASS
    }
  }
}
