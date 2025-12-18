package org.uma.jmetal.component.catalogue.common.solutionscreation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.ChaosBasedSolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;

@DisplayName("ChaosBasedSolutionsCreation Tests")
class ChaosBasedSolutionsCreationTest {

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
      ChaosBasedSolutionsCreation creation = new ChaosBasedSolutionsCreation(problem, 10);

      // Assert
      assertNotNull(creation);
      assertEquals(10, creation.getNumberOfSolutionsToCreate());
    }
  }

  @Nested
  @DisplayName("Create Tests")
  class CreateTests {

    @Test
    @DisplayName("Given requested size, then create exact number of solutions")
    void givenRequestedSize_thenCreateExactNumberOfSolutions() {
      // Arrange
      ChaosBasedSolutionsCreation creation = new ChaosBasedSolutionsCreation(problem, 50);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      assertEquals(50, solutions.size());
    }

    @Test
    @DisplayName("Given solutions created, then all should be within bounds")
    void givenSolutionsCreated_thenAllShouldBeWithinBounds() {
      // Arrange
      ChaosBasedSolutionsCreation creation = new ChaosBasedSolutionsCreation(problem, 100);

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
    @DisplayName("Given chaotic solutions, then all should be unique")
    void givenChaoticSolutions_thenAllShouldBeUnique() {
      // Arrange
      ChaosBasedSolutionsCreation creation = new ChaosBasedSolutionsCreation(problem, 50);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      Set<String> uniquePoints = new HashSet<>();
      for (DoubleSolution solution : solutions) {
        StringBuilder key = new StringBuilder();
        for (int j = 0; j < problem.numberOfVariables(); j++) {
          key.append(String.format("%.15f,", solution.variables().get(j)));
        }
        uniquePoints.add(key.toString());
      }
      assertEquals(solutions.size(), uniquePoints.size(), "All chaotic points should be unique");
    }

    @Test
    @DisplayName("Given zero solutions requested, then create empty list")
    void givenZeroSolutionsRequested_thenCreateEmptyList() {
      // Arrange
      ChaosBasedSolutionsCreation creation = new ChaosBasedSolutionsCreation(problem, 0);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      assertTrue(solutions.isEmpty());
    }

    @Test
    @DisplayName("Given single solution requested, then create one solution")
    void givenSingleSolutionRequested_thenCreateOneSolution() {
      // Arrange
      ChaosBasedSolutionsCreation creation = new ChaosBasedSolutionsCreation(problem, 1);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      assertEquals(1, solutions.size());
    }
  }

  @Nested
  @DisplayName("Coverage Tests")
  class CoverageTests {

    @Test
    @DisplayName("Given many solutions, then coverage should span the search space")
    void givenManySolutions_thenCoverageShouldSpanSearchSpace() {
      // Arrange
      ChaosBasedSolutionsCreation creation = new ChaosBasedSolutionsCreation(problem, 100);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert - check that values span a reasonable range
      for (int j = 0; j < problem.numberOfVariables(); j++) {
        Bounds<Double> bounds = problem.variableBounds().get(j);
        double range = bounds.getUpperBound() - bounds.getLowerBound();

        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;

        for (DoubleSolution solution : solutions) {
          double value = solution.variables().get(j);
          minValue = Math.min(minValue, value);
          maxValue = Math.max(maxValue, value);
        }

        double coverage = (maxValue - minValue) / range;
        assertTrue(coverage > 0.5, "Variable " + j + " should cover at least 50% of range");
      }
    }

    @Test
    @DisplayName("Given chaotic sequence, then values should not cluster")
    void givenChaoticSequence_thenValuesShouldNotCluster() {
      // Arrange
      DoubleProblem problem1D = new FakeDoubleProblem(1, 2, 0);
      ChaosBasedSolutionsCreation creation = new ChaosBasedSolutionsCreation(problem1D, 100);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert - divide range into 10 bins and check distribution
      int[] bins = new int[10];
      Bounds<Double> bounds = problem1D.variableBounds().get(0);
      double range = bounds.getUpperBound() - bounds.getLowerBound();

      for (DoubleSolution solution : solutions) {
        double normalizedValue =
            (solution.variables().get(0) - bounds.getLowerBound()) / range;
        int binIndex = Math.min(9, (int) (normalizedValue * 10));
        bins[binIndex]++;
      }

      // Check that no bin has more than 30% of solutions (would indicate clustering)
      for (int bin : bins) {
        assertTrue(bin < 30, "Solutions should not cluster in a single region");
      }
    }
  }

  @Nested
  @DisplayName("High Dimensional Tests")
  class HighDimensionalTests {

    @Test
    @DisplayName("Given high dimensional problem, then create valid solutions")
    void givenHighDimensionalProblem_thenCreateValidSolutions() {
      // Arrange
      DoubleProblem problemHighD = new FakeDoubleProblem(30, 2, 0);
      ChaosBasedSolutionsCreation creation = new ChaosBasedSolutionsCreation(problemHighD, 100);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      assertEquals(100, solutions.size());
      for (DoubleSolution solution : solutions) {
        assertEquals(30, solution.variables().size());
        for (int j = 0; j < problemHighD.numberOfVariables(); j++) {
          Bounds<Double> bounds = problemHighD.variableBounds().get(j);
          double value = solution.variables().get(j);
          assertTrue(value >= bounds.getLowerBound() && value <= bounds.getUpperBound());
        }
      }
    }
  }
}
