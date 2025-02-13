package org.uma.jmetal.problem.multiobjective.multiobjectiveknapsack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

import static org.junit.jupiter.api.Assertions.*;

class MultiObjectiveKnapsackTest {
  @Nested
  @DisplayName("When the constructor is called")
  class ConstructorTestCases {
    @Test
    @DisplayName("An instance is correctly created")
    void TheConstructorCorrectlyCreatesAProblemInstance() {
      // Arrange
      int[] profitsA = {10, 5, 15, 7};
      int[] profitsB = {2, 6, 3, 4};
      int[][] profits = {profitsA, profitsB};
      int[] weightsA = {2, 3, 5, 7};
      int[] weightsB = {1, 5, 6, 3};
      int[] weightsC = {4, 1, 2, 4};
      int[][] weights = {weightsA, weightsB, weightsC};

      int[] capacities = {5, 10, 6};

      // Act
      var problem = new MultiObjectiveKnapsack(profits, weights, capacities);
      // Assert
      assertArrayEquals(capacities, problem.capacities());
      assertArrayEquals(profitsA, problem.objectives()[0]);
      assertArrayEquals(profitsB, problem.objectives()[1]);
      assertArrayEquals(weightsA, problem.constraints()[0]);
      assertArrayEquals(weightsB, problem.constraints()[1]);
      assertArrayEquals(weightsC, problem.constraints()[2]);

      assertEquals(1, problem.numberOfVariables());
      assertEquals(profits.length, problem.numberOfObjectives());
      assertEquals(weights.length, problem.numberOfConstraints());

      assertEquals(1, problem.numberOfBitsPerVariable().size());
      assertEquals(profitsA.length, problem.numberOfBitsPerVariable().get(0));
      assertEquals(profitsB.length, problem.numberOfBitsPerVariable().get(0));
      assertEquals(weightsA.length, problem.numberOfBitsPerVariable().get(0));
      assertEquals(weightsB.length, problem.numberOfBitsPerVariable().get(0));
      assertEquals(weightsC.length, problem.numberOfBitsPerVariable().get(0));
    }

    @Test
    @DisplayName("An exception is raised if the sizes of the profits vectors are not equal")
    void whenTheSizesOfProfitsAreNotEqualThenAnExceptionIsRaised() {
      // Arrange
      int[] profitsA = {10, 5};
      int[] profitsB = {2, 6, 3, 4};
      int[][] profits = {profitsA, profitsB};
      int[] weightsA = {2, 3, 5, 7};
      int[] weightsB = {1, 5, 6, 3};
      int[] weightsC = {4, 1, 2, 4};
      int[][] weights = {weightsA, weightsB, weightsC};

      int[] capacities = {5, 10, 6};
      // Assert
      Exception exception =
          assertThrows(
              InvalidConditionException.class,
              () -> new MultiObjectiveKnapsack(profits, weights, capacities));
      String expectedMessage = "The objective vectors must have the same size";
      assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("An exception is raised if the sizes of the weight vectors are not equal")
    void whenTheSizesOfWeightsAreNotEqualThenAnExceptionIsRaised() {
      // Arrange
      int[] profitsA = {10, 5, 3, 5};
      int[] profitsB = {2, 6, 3, 4};
      int[][] profits = {profitsA, profitsB};
      int[] weightsA = {2, 3, 5, 7};
      int[] weightsB = {1, 5, 3};
      int[] weightsC = {4, 1, 2, 4};
      int[][] weights = {weightsA, weightsB, weightsC};

      int[] capacities = {5, 10, 6};

      // Assert
      Exception exception =
          assertThrows(
              InvalidConditionException.class,
              () -> new MultiObjectiveKnapsack(profits, weights, capacities));
      String expectedMessage = "The constraint vectors must have the same size";
      assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName(
        "An exception is raised if the sizes of the profits and weight vectors are not equal")
    void whenTheSizesOfWeightsAndProfitsAreNotEqualThenAnExceptionIsRaised() {
      // Arrange
      int[] profitsA = {10, 5, 3, 5};
      int[] profitsB = {2, 6, 3, 4};
      int[][] profits = {profitsA, profitsB};
      int[] weightsA = {2, 3, 5};
      int[] weightsB = {1, 5, 3};
      int[] weightsC = {4, 1, 2};
      int[][] weights = {weightsA, weightsB, weightsC};

      int[] capacities = {5, 10, 6};

      // Assert
      Exception exception =
          assertThrows(
              InvalidConditionException.class,
              () -> new MultiObjectiveKnapsack(profits, weights, capacities));
      String expectedMessage =
          "The number of objectives ("
              + profits[0].length
              + ") must be equal to the number of constraints ("
              + weights[0].length
              + ")";
      assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("An exception is raised if some of the capacities are negative")
    void whenACapacityIsNegativeAnExceptionIsRaised() {
      // Arrange
      int[] profitsA = {10, 5, 3, 5};
      int[] profitsB = {2, 6, 3, 4};
      int[][] profits = {profitsA, profitsB};
      int[] weightsA = {2, 3, 5, 7};
      int[] weightsB = {1, 5, 6, 3};
      int[] weightsC = {4, 1, 2, 4};
      int[][] weights = {weightsA, weightsB, weightsC};

      int[] capacities = {5, -2, 6};

      // Assert
      Exception exception =
          assertThrows(
              InvalidConditionException.class,
              () -> new MultiObjectiveKnapsack(profits, weights, capacities));
      String expectedMessage = "All the capacities must be greater than zero";
      assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("An exception is raised if some of the capacities is zero")
    void whenACapacityIsZeroAnExceptionIsRaised() {
      // Arrange
      int[] profitsA = {10, 5, 3, 5};
      int[] profitsB = {2, 6, 3, 4};
      int[][] profits = {profitsA, profitsB};
      int[] weightsA = {2, 3, 5, 7};
      int[] weightsB = {1, 5, 6, 3};
      int[] weightsC = {4, 1, 2, 4};
      int[][] weights = {weightsA, weightsB, weightsC};

      int[] capacities = {5, 2, 0};

      // Assert
      Exception exception =
          assertThrows(
              InvalidConditionException.class,
              () -> new MultiObjectiveKnapsack(profits, weights, capacities));
      String expectedMessage = "All the capacities must be greater than zero";
      assertEquals(expectedMessage, exception.getMessage());
    }
  }

  @Nested
  @DisplayName("When a solution is evaluated")
  class EvaluateTestCases {

    MultiObjectiveKnapsack problem;

    @BeforeEach
    void setup() {
      // Arrange
      int[] profitsA = {10, 5, 15, 7};
      int[] profitsB = {2, 6, 3, 4};
      int[][] profits = {profitsA, profitsB};
      int[] weightsA = {2, 3, 5, 7};
      int[] weightsB = {1, 5, 6, 3};
      int[] weightsC = {3, 1, 2, 4};
      int[][] weights = {weightsA, weightsB, weightsC};

      int[] capacities = {12, 9, 5};

      problem = new MultiObjectiveKnapsack(profits, weights, capacities);
    }

    @Test
    @DisplayName("if the solution is feasible, the objective values are correctly computed")
    void whenEvaluatingAFeasibleSolutionThenTheObjectiveValueIsCorrect() {
      // Arrange
      BinarySet binarySet = new BinarySet(problem.numberOfBitsPerVariable().get(0));
      binarySet.set(0);
      binarySet.clear(1);
      binarySet.set(2);
      binarySet.clear(3);

      BinarySolution solution = problem.createSolution();
      solution.variables().set(0, binarySet);

      // Act
      problem.evaluate(solution);

      // Assert
      int[] expectedObjectiveValues = {-1 * 25, -1 * 5};
      assertEquals(expectedObjectiveValues[0], solution.objectives()[0]);
      assertEquals(expectedObjectiveValues[1], solution.objectives()[1]);
      assertEquals(0, solution.constraints()[0]);

      assertTrue(ConstraintHandling.isFeasible(solution));
      assertTrue(ConstraintHandling.overallConstraintViolationDegree(solution) == 0);
    }

    @Test
    @DisplayName(
        "if the solution is not feasible for violating two constraints, the constraints values are correctly computed")
    void
        whenEvaluatingAnUFeasibleSolutionForViolatingTwoConstraintsThenTheConstraintValueIsCorrect() {
      // Arrange
      BinarySet binarySet = new BinarySet(problem.numberOfBitsPerVariable().get(0));
      binarySet.set(0);
      binarySet.set(1);
      binarySet.set(2);
      binarySet.clear(3);

      BinarySolution solution = problem.createSolution();
      solution.variables().set(0, binarySet);

      // Act
      problem.evaluate(solution);

      // Assert
      int[] expectedConstraintValues = {0, -3, -1};
      assertEquals(expectedConstraintValues[0], solution.constraints()[0]);
      assertEquals(expectedConstraintValues[1], solution.constraints()[1]);
      assertEquals(expectedConstraintValues[2], solution.constraints()[2]);

      assertEquals(2, ConstraintHandling.numberOfViolatedConstraints(solution));
      assertEquals(-4, ConstraintHandling.overallConstraintViolationDegree(solution));
    }

    @Test
    @DisplayName(
        "if the solution is not feasible for violating all the constraints, the constraints values are correctly computed")
    void whenEvaluatingAnUnFeasibleSolutionThenTheConstraintValueIsCorrect() {
      // Arrange
      BinarySet binarySet = new BinarySet(problem.numberOfBitsPerVariable().get(0));
      binarySet.set(0);
      binarySet.set(1);
      binarySet.set(2);
      binarySet.set(3);

      BinarySolution solution = problem.createSolution();
      solution.variables().set(0, binarySet);

      // Act
      problem.evaluate(solution);

      // Assert
      int[] expectedConstraintValues = {-5, -6, -5};
      assertEquals(expectedConstraintValues[0], solution.constraints()[0]);
      assertEquals(expectedConstraintValues[1], solution.constraints()[1]);
      assertEquals(expectedConstraintValues[2], solution.constraints()[2]);

      assertEquals(3, ConstraintHandling.numberOfViolatedConstraints(solution));
      assertEquals(-16, ConstraintHandling.overallConstraintViolationDegree(solution));
    }
  }
}
