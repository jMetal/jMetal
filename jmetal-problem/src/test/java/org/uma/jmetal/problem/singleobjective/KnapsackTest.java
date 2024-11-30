package org.uma.jmetal.problem.singleobjective;

import org.junit.jupiter.api.*;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

import static org.junit.jupiter.api.Assertions.*;

class KnapsackTest {
  @Nested
  @DisplayName("When the constructor is called")
  class ConstructorTestCases {
    @Test
    @DisplayName("An instance is correctly created")
    void TheConstructorCorrectlyCreatesAProblemInstance() {
      // Arrange
      int[] profits = {10, 5, 15, 7, 6, 18, 3};
      int[] weights = {2, 3, 5, 7, 1, 4, 1};
      int capacity = 15;

      // Act
      Knapsack problem = new Knapsack(profits, weights, capacity);

      // Assert
      assertEquals(capacity, problem.capacity());
      assertArrayEquals(profits, problem.profits());
      assertArrayEquals(weights, problem.weights());

      assertEquals(1, problem.numberOfVariables());
      assertEquals(1, problem.numberOfObjectives());
      assertEquals(1, problem.numberOfConstraints());

      assertEquals(1, problem.numberOfBitsPerVariable().size());
      assertEquals(profits.length, problem.numberOfBitsPerVariable().get(0));
    }

    @Test
    @DisplayName(
        "An exception is raised if the sizes of the profits and weight vectors are not equal")
    void whenTheSizesOfWeightsAndProfitsAreNotEqualThenAnExceptionIsRaised() {
      // Arrange
      int[] profits = {10, 5, 15, 7, 6, 18, 3};
      int[] weights = {2, 3, 5, 7, 1, 1};
      int capacity = 15;

      // Assert
      Exception exception =
          assertThrows(
              InvalidConditionException.class, () -> new Knapsack(profits, weights, capacity));
      String expectedMessage =
          "The number of profits ("
              + profits.length
              + ") must be equal to the number of weights ("
              + weights.length
              + ")";
      assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("An exception is raised if the capacity is negative")
    void whenTheCapacityIsNegativeAnExceptionIsRaised() {
      // Arrange
      int[] profits = {10, 5, 15, 7, 6, 18, 3};
      int[] weights = {2, 3, 5, 7, 1, 4, 1};
      int capacity = -1;

      // Assert
      Exception exception =
          assertThrows(
              InvalidConditionException.class, () -> new Knapsack(profits, weights, capacity));
      String expectedMessage = "The capacity (" + capacity + ") must be greater than zero";
      assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("An exception is raised if the capacity is zero")
    void whenTheCapacityIsZeroAnExceptionIsRaised() {
      // Arrange
      int[] profits = {10, 5, 15, 7, 6, 18, 3};
      int[] weights = {2, 3, 5, 7, 1, 4, 1};
      int capacity = 0;

      // Assert
      Exception exception =
          assertThrows(
              InvalidConditionException.class, () -> new Knapsack(profits, weights, capacity));
      String expectedMessage = "The capacity (" + capacity + ") must be greater than zero";
      assertEquals(expectedMessage, exception.getMessage());
    }
  }

  @Nested
  @DisplayName("When a solution is evaluated")
  class EvaluateTestCases {

    Knapsack problem;

    @BeforeEach
    void setup() {
      int[] profits = {10, 5, 15, 7, 6, 18, 3};
      int[] weights = {2, 3, 5, 7, 1, 4, 1};
      int capacity = 15;

      problem = new Knapsack(profits, weights, capacity);
    }

    @Test
    @DisplayName("if the solution is feasible, the objective value is correctly computed")
    void whenEvaluatingAFeasibleSolutionThenTheObjectiveValueIsCorrect() {
      // Arrange
      BinarySet binarySet = new BinarySet(problem.numberOfBitsPerVariable().get(0));
      binarySet.set(0);
      binarySet.clear(1);
      binarySet.set(2);
      binarySet.clear(3);
      binarySet.clear(4);
      binarySet.set(5);
      binarySet.set(6);

      BinarySolution solution = problem.createSolution();
      solution.variables().set(0, binarySet);

      // Act
      problem.evaluate(solution);

      // Assert
      int expectedObjectiveValue = -1 * (46);
      assertEquals(expectedObjectiveValue, solution.objectives()[0]);
      assertEquals(0, solution.constraints()[0]);
    }

    @Test
    @DisplayName("if the solution is not feasible, the constraint value is correctly computed")
    void whenEvaluatingAnUFeasibleSolutionThenTheConstraintValueIsCorrect() {
      // Arrange
      BinarySet binarySet = new BinarySet(problem.numberOfBitsPerVariable().get(0));
      binarySet.set(0);
      binarySet.set(1);
      binarySet.set(2);
      binarySet.set(3);
      binarySet.clear(4);
      binarySet.clear(5);
      binarySet.clear(6);

      BinarySolution solution = problem.createSolution();
      solution.variables().set(0, binarySet);

      // Act
      problem.evaluate(solution);

      // Assert
      int expectedConstraintValue = -2;
      int expectedObjectiveValue = -1 * (37);
      assertEquals(expectedConstraintValue, solution.constraints()[0]);
      assertEquals(expectedObjectiveValue, solution.objectives()[0]);
    }
  }
}
