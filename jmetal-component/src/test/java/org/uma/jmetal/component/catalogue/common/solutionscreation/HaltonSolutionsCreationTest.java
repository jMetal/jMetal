package org.uma.jmetal.component.catalogue.common.solutionscreation;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.HaltonSolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("HaltonSolutionsCreation Tests")
class HaltonSolutionsCreationTest {

  private DoubleProblem problem;

  @BeforeEach
  void setUp() {
    problem = new FakeDoubleProblem(3, 2, 0);
    JMetalRandom.getInstance().setSeed(42L);
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("Given valid parameters, then creation should succeed")
    void givenValidParameters_thenCreationShouldSucceed() {
      // Arrange & Act
      HaltonSolutionsCreation creation = new HaltonSolutionsCreation(problem, 10);

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
      HaltonSolutionsCreation creation = new HaltonSolutionsCreation(problem, 50);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      assertEquals(50, solutions.size());
    }

    @Test
    @DisplayName("Given non-power-of-two size, then create exact number of solutions")
    void givenNonPowerOfTwoSize_thenCreateExactNumberOfSolutions() {
      // Arrange
      HaltonSolutionsCreation creation = new HaltonSolutionsCreation(problem, 37);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      assertEquals(37, solutions.size());
    }

    @Test
    @DisplayName("Given solutions created, then all should be within bounds")
    void givenSolutionsCreated_thenAllShouldBeWithinBounds() {
      // Arrange
      HaltonSolutionsCreation creation = new HaltonSolutionsCreation(problem, 100);

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
    @DisplayName("Given Halton solutions, then all should be unique")
    void givenHaltonSolutions_thenAllShouldBeUnique() {
      // Arrange
      HaltonSolutionsCreation creation = new HaltonSolutionsCreation(problem, 50);

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
      assertEquals(solutions.size(), uniquePoints.size(), "All Halton points should be unique");
    }

    @Test
    @DisplayName("Given single solution requested, then create one solution")
    void givenSingleSolutionRequested_thenCreateOneSolution() {
      // Arrange
      HaltonSolutionsCreation creation = new HaltonSolutionsCreation(problem, 1);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      assertEquals(1, solutions.size());
    }
  }

  @Nested
  @DisplayName("Reproducibility Tests")
  class ReproducibilityTests {

    @Test
    @DisplayName("Given same seed, then create identical solutions")
    void givenSameSeed_thenCreateIdenticalSolutions() {
      // Arrange
      HaltonSolutionsCreation creation = new HaltonSolutionsCreation(problem, 20);

      // Act
      JMetalRandom.getInstance().setSeed(123L);
      List<DoubleSolution> solutions1 = creation.create();

      JMetalRandom.getInstance().setSeed(123L);
      List<DoubleSolution> solutions2 = creation.create();

      // Assert
      assertEquals(solutions1.size(), solutions2.size());
      for (int i = 0; i < solutions1.size(); i++) {
        for (int j = 0; j < problem.numberOfVariables(); j++) {
          assertEquals(
              solutions1.get(i).variables().get(j),
              solutions2.get(i).variables().get(j),
              1e-15,
              "Solutions should be identical with same seed");
        }
      }
    }

    @Test
    @DisplayName("Given different seeds, then create different solutions")
    void givenDifferentSeeds_thenCreateDifferentSolutions() {
      // Arrange
      HaltonSolutionsCreation creation = new HaltonSolutionsCreation(problem, 20);

      // Act
      JMetalRandom.getInstance().setSeed(123L);
      List<DoubleSolution> solutions1 = creation.create();

      JMetalRandom.getInstance().setSeed(456L);
      List<DoubleSolution> solutions2 = creation.create();

      // Assert
      boolean foundDifference = false;
      for (int i = 0; i < solutions1.size() && !foundDifference; i++) {
        for (int j = 0; j < problem.numberOfVariables(); j++) {
          if (Math.abs(
                  solutions1.get(i).variables().get(j) - solutions2.get(i).variables().get(j))
              > 1e-10) {
            foundDifference = true;
            break;
          }
        }
      }
      assertTrue(foundDifference, "Different seeds should produce different solutions");
    }
  }

  @Nested
  @DisplayName("Space Coverage Tests")
  class SpaceCoverageTests {

    @Test
    @DisplayName("Given Halton samples, then coverage should be complete")
    void givenHaltonSamples_thenCoverageShouldBeComplete() {
      // Arrange
      int numSamples = 64;
      HaltonSolutionsCreation creation = new HaltonSolutionsCreation(problem, numSamples);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      for (int j = 0; j < problem.numberOfVariables(); j++) {
        Bounds<Double> bounds = problem.variableBounds().get(j);
        double minValue = Double.MAX_VALUE;
        double maxValue = -Double.MAX_VALUE;
        for (DoubleSolution solution : solutions) {
          double value = solution.variables().get(j);
          minValue = Math.min(minValue, value);
          maxValue = Math.max(maxValue, value);
        }
        double range = bounds.getUpperBound() - bounds.getLowerBound();
        double coverage = (maxValue - minValue) / range;
        assertTrue(
            coverage > 0.9,
            "Variable " + j + " should cover at least 90% of range, but covers " + coverage);
      }
    }

    @Test
    @DisplayName("Given Halton samples, then distribution should not cluster")
    void givenHaltonSamples_thenDistributionShouldNotCluster() {
      // Arrange
      DoubleProblem problem1D = new FakeDoubleProblem(1, 2, 0);
      int numSamples = 100;
      HaltonSolutionsCreation creation = new HaltonSolutionsCreation(problem1D, numSamples);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert - divide range into 10 bins and check distribution
      int numBins = 10;
      int[] bins = new int[numBins];
      Bounds<Double> bounds = problem1D.variableBounds().get(0);
      double range = bounds.getUpperBound() - bounds.getLowerBound();

      for (DoubleSolution solution : solutions) {
        double normalizedValue = (solution.variables().get(0) - bounds.getLowerBound()) / range;
        int binIndex = Math.min(numBins - 1, (int) (normalizedValue * numBins));
        bins[binIndex]++;
      }

      // Each bin should have approximately numSamples/numBins = 10 samples
      for (int i = 0; i < numBins; i++) {
        assertTrue(
            bins[i] >= 5 && bins[i] <= 20,
            "Bin " + i + " should have between 5-20 samples, but has " + bins[i]);
      }
    }

    @Test
    @DisplayName("Given Halton samples, then minimum distance should be reasonable")
    void givenHaltonSamples_thenMinimumDistanceShouldBeReasonable() {
      // Arrange
      int numSamples = 50;
      HaltonSolutionsCreation creation = new HaltonSolutionsCreation(problem, numSamples);

      // Act
      List<DoubleSolution> solutions = creation.create();

      // Assert
      double minDistance = Double.MAX_VALUE;
      for (int i = 0; i < solutions.size(); i++) {
        for (int j = i + 1; j < solutions.size(); j++) {
          double distance = 0.0;
          for (int var = 0; var < problem.numberOfVariables(); var++) {
            Bounds<Double> bounds = problem.variableBounds().get(var);
            double range = bounds.getUpperBound() - bounds.getLowerBound();
            double normalizedDiff =
                (solutions.get(i).variables().get(var) - solutions.get(j).variables().get(var))
                    / range;
            distance += normalizedDiff * normalizedDiff;
          }
          distance = Math.sqrt(distance);
          minDistance = Math.min(minDistance, distance);
        }
      }
      assertTrue(
          minDistance > 0.01,
          "Halton should maintain reasonable spacing, min distance: " + minDistance);
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
      HaltonSolutionsCreation creation = new HaltonSolutionsCreation(problemHighD, 100);

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
