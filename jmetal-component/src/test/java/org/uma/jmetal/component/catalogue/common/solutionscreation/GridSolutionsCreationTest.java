package org.uma.jmetal.component.catalogue.common.solutionscreation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.GridSolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;

@DisplayName("GridSolutionsCreation Tests")
class GridSolutionsCreationTest {

  private DoubleProblem problem;

  @BeforeEach
  void setUp() {
    problem = new FakeDoubleProblem(2, 2, 0); // 2 variables, 2 objectives, 0 constraints
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("Given valid parameters, then creation should succeed")
    void givenValidParameters_thenCreationShouldSucceed() {
      // Arrange & Act
      GridSolutionsCreation creation = new GridSolutionsCreation(problem, 10);

      // Assert
      assertNotNull(creation);
      assertEquals(10, creation.getNumberOfSolutionsToCreate());
    }
  }

  @Nested
  @DisplayName("Create Tests")
  class CreateTests {

    @Test
    @DisplayName("Given requested size, then create at most that many solutions")
    void givenRequestedSize_thenCreateAtMostThatManySolutions() {
      // Arrange
      GridSolutionsCreation creation = new GridSolutionsCreation(problem, 10);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      assertTrue(solutions.size() <= 10);
    }

    @Test
    @DisplayName("Given solutions created, then all should be within bounds")
    void givenSolutionsCreated_thenAllShouldBeWithinBounds() {
      // Arrange
      GridSolutionsCreation creation = new GridSolutionsCreation(problem, 16);

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
    @DisplayName("Given 2D problem with 4 solutions, then create 2x2 grid")
    void given2DProblemWith4Solutions_thenCreate2x2Grid() {
      // Arrange
      GridSolutionsCreation creation = new GridSolutionsCreation(problem, 4);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      assertEquals(4, solutions.size());
    }

    @Test
    @DisplayName("Given grid solutions, then they should be deterministic")
    void givenGridSolutions_thenTheyShouldBeDeterministic() {
      // Arrange
      GridSolutionsCreation creation1 = new GridSolutionsCreation(problem, 9);
      GridSolutionsCreation creation2 = new GridSolutionsCreation(problem, 9);

      // Act
      List<DoubleSolution> solutions1 = creation1.create();
      List<DoubleSolution> solutions2 = creation2.create();

      // Assert
      assertEquals(solutions1.size(), solutions2.size());
      for (int i = 0; i < solutions1.size(); i++) {
        for (int j = 0; j < problem.numberOfVariables(); j++) {
          assertEquals(
              solutions1.get(i).variables().get(j),
              solutions2.get(i).variables().get(j),
              1e-10);
        }
      }
    }

    @Test
    @DisplayName("Given grid solutions, then all should be unique")
    void givenGridSolutions_thenAllShouldBeUnique() {
      // Arrange
      GridSolutionsCreation creation = new GridSolutionsCreation(problem, 16);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      Set<String> uniquePoints = new HashSet<>();
      for (DoubleSolution solution : solutions) {
        StringBuilder key = new StringBuilder();
        for (int j = 0; j < problem.numberOfVariables(); j++) {
          key.append(String.format("%.10f,", solution.variables().get(j)));
        }
        uniquePoints.add(key.toString());
      }
      assertEquals(solutions.size(), uniquePoints.size(), "All grid points should be unique");
    }
  }

  @Nested
  @DisplayName("Grid Coverage Tests")
  class GridCoverageTests {

    @Test
    @DisplayName("Given grid solutions, then corners should be included")
    void givenGridSolutions_thenCornersShouldBeIncluded() {
      // Arrange
      GridSolutionsCreation creation = new GridSolutionsCreation(problem, 4);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert - check that bounds are covered
      Bounds<Double> bounds0 = problem.variableBounds().get(0);
      Bounds<Double> bounds1 = problem.variableBounds().get(1);

      boolean hasLowerLower = false;
      boolean hasUpperUpper = false;

      for (DoubleSolution solution : solutions) {
        double v0 = solution.variables().get(0);
        double v1 = solution.variables().get(1);

        if (Math.abs(v0 - bounds0.getLowerBound()) < 1e-10
            && Math.abs(v1 - bounds1.getLowerBound()) < 1e-10) {
          hasLowerLower = true;
        }
        if (Math.abs(v0 - bounds0.getUpperBound()) < 1e-10
            && Math.abs(v1 - bounds1.getUpperBound()) < 1e-10) {
          hasUpperUpper = true;
        }
      }

      assertTrue(hasLowerLower, "Grid should include lower-lower corner");
      assertTrue(hasUpperUpper, "Grid should include upper-upper corner");
    }

    @Test
    @DisplayName("Given grid solutions, then spacing should be uniform")
    void givenGridSolutions_thenSpacingShouldBeUniform() {
      // Arrange - 3 variables, 8 solutions -> 2 divisions per dimension
      DoubleProblem problem3D = new FakeDoubleProblem(3, 2, 0);
      GridSolutionsCreation creation = new GridSolutionsCreation(problem3D, 8);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert - with 2 divisions, each variable should only have 2 distinct values
      for (int j = 0; j < problem3D.numberOfVariables(); j++) {
        Set<Double> distinctValues = new HashSet<>();
        for (DoubleSolution solution : solutions) {
          distinctValues.add(Math.round(solution.variables().get(j) * 1e10) / 1e10);
        }
        assertEquals(2, distinctValues.size(),
            "Variable " + j + " should have exactly 2 distinct values");
      }
    }
  }

  @Nested
  @DisplayName("Edge Cases Tests")
  class EdgeCasesTests {

    @Test
    @DisplayName("Given single variable problem, then create linear grid")
    void givenSingleVariableProblem_thenCreateLinearGrid() {
      // Arrange
      DoubleProblem problem1D = new FakeDoubleProblem(1, 2, 0);
      GridSolutionsCreation creation = new GridSolutionsCreation(problem1D, 5);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      assertTrue(solutions.size() <= 5);
      assertTrue(solutions.size() >= 2); // At least 2 divisions
    }

    @Test
    @DisplayName("Given high dimensional problem, then still create valid solutions")
    void givenHighDimensionalProblem_thenStillCreateValidSolutions() {
      // Arrange
      DoubleProblem problemHighD = new FakeDoubleProblem(10, 2, 0);
      GridSolutionsCreation creation = new GridSolutionsCreation(problemHighD, 100);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      assertTrue(solutions.size() <= 100);
      for (DoubleSolution solution : solutions) {
        for (int j = 0; j < problemHighD.numberOfVariables(); j++) {
          Bounds<Double> bounds = problemHighD.variableBounds().get(j);
          double value = solution.variables().get(j);
          assertTrue(value >= bounds.getLowerBound() && value <= bounds.getUpperBound());
        }
      }
    }
  }
}
