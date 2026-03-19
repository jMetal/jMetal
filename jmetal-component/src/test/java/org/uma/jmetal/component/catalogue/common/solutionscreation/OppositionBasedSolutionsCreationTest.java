package org.uma.jmetal.component.catalogue.common.solutionscreation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.OppositionBasedSolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;

@DisplayName("OppositionBasedSolutionsCreation Tests")
class OppositionBasedSolutionsCreationTest {

  private DoubleProblem problem;

  @BeforeEach
  void setUp() {
    problem = new FakeDoubleProblem(3, 2, 0); // 3 variables, 2 objectives, 0 constraints
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("Given valid parameters, then creation should succeed")
    void givenValidParameters_thenCreationShouldSucceed() {
      // Arrange & Act
      OppositionBasedSolutionsCreation creation =
          new OppositionBasedSolutionsCreation(problem, 10);

      // Assert
      assertNotNull(creation);
      assertEquals(10, creation.getNumberOfSolutionsToCreate());
    }
  }

  @Nested
  @DisplayName("Create Tests")
  class CreateTests {

    @Test
    @DisplayName("Given even number of solutions, then create correct count")
    void givenEvenNumberOfSolutions_thenCreateCorrectCount() {
      // Arrange
      OppositionBasedSolutionsCreation creation =
          new OppositionBasedSolutionsCreation(problem, 10);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      assertEquals(10, solutions.size());
    }

    @Test
    @DisplayName("Given odd number of solutions, then create correct count")
    void givenOddNumberOfSolutions_thenCreateCorrectCount() {
      // Arrange
      OppositionBasedSolutionsCreation creation =
          new OppositionBasedSolutionsCreation(problem, 11);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      assertEquals(11, solutions.size());
    }

    @Test
    @DisplayName("Given solutions created, then all should be within bounds")
    void givenSolutionsCreated_thenAllShouldBeWithinBounds() {
      // Arrange
      OppositionBasedSolutionsCreation creation =
          new OppositionBasedSolutionsCreation(problem, 20);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      for (DoubleSolution solution : solutions) {
        for (int j = 0; j < problem.numberOfVariables(); j++) {
          Bounds<Double> bounds = problem.variableBounds().get(j);
          double value = solution.variables().get(j);
          assertTrue(
              value >= bounds.getLowerBound() && value <= bounds.getUpperBound(),
              "Variable " + j + " value " + value + " out of bounds");
        }
      }
    }

    @Test
    @DisplayName("Given pair of solutions, then second should be opposite of first")
    void givenPairOfSolutions_thenSecondShouldBeOppositeOfFirst() {
      // Arrange
      OppositionBasedSolutionsCreation creation =
          new OppositionBasedSolutionsCreation(problem, 2);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      DoubleSolution original = solutions.get(0);
      DoubleSolution opposite = solutions.get(1);

      for (int j = 0; j < problem.numberOfVariables(); j++) {
        Bounds<Double> bounds = problem.variableBounds().get(j);
        double lb = bounds.getLowerBound();
        double ub = bounds.getUpperBound();
        double originalValue = original.variables().get(j);
        double oppositeValue = opposite.variables().get(j);

        // opposite = lb + ub - original
        double expectedOpposite = lb + ub - originalValue;
        assertEquals(expectedOpposite, oppositeValue, 1e-10,
            "Variable " + j + " opposite value incorrect");
      }
    }

    @Test
    @DisplayName("Given single solution requested, then create one solution")
    void givenSingleSolutionRequested_thenCreateOneSolution() {
      // Arrange
      OppositionBasedSolutionsCreation creation =
          new OppositionBasedSolutionsCreation(problem, 1);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      assertEquals(1, solutions.size());
    }

    @Test
    @DisplayName("Given zero solutions requested, then create empty list")
    void givenZeroSolutionsRequested_thenCreateEmptyList() {
      // Arrange
      OppositionBasedSolutionsCreation creation =
          new OppositionBasedSolutionsCreation(problem, 0);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      assertTrue(solutions.isEmpty());
    }
  }

  @Nested
  @DisplayName("Opposition Property Tests")
  class OppositionPropertyTests {

    @Test
    @DisplayName("Given opposite solutions, then sum of original and opposite equals lb + ub")
    void givenOppositeSolutions_thenSumEqualsLbPlusUb() {
      // Arrange
      OppositionBasedSolutionsCreation creation =
          new OppositionBasedSolutionsCreation(problem, 10);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert - check pairs (0,1), (2,3), (4,5), (6,7), (8,9)
      for (int i = 0; i < 10; i += 2) {
        DoubleSolution original = solutions.get(i);
        DoubleSolution opposite = solutions.get(i + 1);

        for (int j = 0; j < problem.numberOfVariables(); j++) {
          Bounds<Double> bounds = problem.variableBounds().get(j);
          double expectedSum = bounds.getLowerBound() + bounds.getUpperBound();
          double actualSum = original.variables().get(j) + opposite.variables().get(j);
          assertEquals(expectedSum, actualSum, 1e-10);
        }
      }
    }
  }
}
