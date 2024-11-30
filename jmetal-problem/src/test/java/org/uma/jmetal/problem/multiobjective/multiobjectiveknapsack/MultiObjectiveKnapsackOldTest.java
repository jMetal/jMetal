package org.uma.jmetal.problem.multiobjective.multiobjectiveknapsack;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MultiObjectiveKnapsackOldTest {
    @Nested
    @DisplayName("When the constructor is called")
    class ConstructorTestCases {
        @Test
        @DisplayName("An instance is correctly created")
        void TheConstructorCorrectlyCreatesAProblemInstance() {
            // Arrange
            int[] profitsA = {10, 5, 15, 7};
            int[] profitsB = {2, 6, 3, 4};
            var profits = List.of(profitsA, profitsB) ;
            int[] weights = {2, 3, 5, 7};
            int capacity = 8;

            // Act
            var problem = new MultiObjectiveKnapsackOld(profits, weights, capacity);

            // Assert
            assertEquals(capacity, problem.capacity());
            assertArrayEquals(profitsA, problem.profits().get(0));
            assertArrayEquals(profitsB, problem.profits().get(1));
            assertArrayEquals(weights, problem.weights());

            assertEquals(1, problem.numberOfVariables());
            assertEquals(2, problem.numberOfObjectives());
            assertEquals(1, problem.numberOfConstraints());

            assertEquals(1, problem.numberOfBitsPerVariable().size());
            assertEquals(profitsA.length, problem.numberOfBitsPerVariable().get(0));
            assertEquals(profitsB.length, problem.numberOfBitsPerVariable().get(0));
        }

        @Test
        @DisplayName(
                "An exception is raised if the sizes of the profits and weight vectors are not equal")
        void whenTheSizesOfWeightsAndProfitsAreNotEqualThenAnExceptionIsRaised() {
            // Arrange
            int[] profitsA = {10, 5, 15, 7};
            int[] profitsB = {2, 6, 3, 4};
            var profits = List.of(profitsA, profitsB) ;
            int[] weights = {2, 3, 5};
            int capacity = 8;

            // Assert
            Exception exception =
                    assertThrows(
                            InvalidConditionException.class, () -> new MultiObjectiveKnapsackOld(profits, weights, capacity));
            String expectedMessage =
                    "The number of profits ("
                            + profits.get(0).length
                            + ") must be equal to the number of weights ("
                            + weights.length
                            + ")";
            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName(
                "An exception is raised if the sizes of the profits vectors are not equal")
        void whenTheSizesOfTheProfitsVectorsAreNotEqualThenAnExceptionIsRaised() {
            // Arrange
            int[] profitsA = {10, 5, 15};
            int[] profitsB = {2, 6, 3, 4};
            var profits = List.of(profitsA, profitsB) ;
            int[] weights = {2, 3, 5};
            int capacity = 8;

            // Assert
            Exception exception =
                    assertThrows(
                            InvalidConditionException.class, () -> new MultiObjectiveKnapsackOld(profits, weights, capacity));
            String expectedMessage =
                    "The profit vectors must have the same size";
            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("An exception is raised if the capacity is negative")
        void whenTheCapacityIsNegativeAnExceptionIsRaised() {
            // Arrange
            int[] profitsA = {10, 5, 15, 7};
            int[] profitsB = {2, 6, 3, 4};
            var profits = List.of(profitsA, profitsB) ;
            int[] weights = {2, 3, 5, 7};
            int capacity = -1;

            // Assert
            Exception exception =
                    assertThrows(
                            InvalidConditionException.class, () -> new MultiObjectiveKnapsackOld(profits, weights, capacity));
            String expectedMessage = "The capacity (" + capacity + ") must be greater than zero";
            assertEquals(expectedMessage, exception.getMessage());
        }

        @Test
        @DisplayName("An exception is raised if the capacity is zero")
        void whenTheCapacityIsZeroAnExceptionIsRaised() {
            // Arrange
            int[] profitsA = {10, 5, 15, 7};
            int[] profitsB = {2, 6, 3, 4};
            var profits = List.of(profitsA, profitsB) ;
            int[] weights = {2, 3, 5, 7};
            int capacity = 0;

            // Assert
            Exception exception =
                    assertThrows(
                            InvalidConditionException.class, () -> new MultiObjectiveKnapsackOld(profits, weights, capacity));
            String expectedMessage = "The capacity (" + capacity + ") must be greater than zero";
            assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("When a solution is evaluated")
    class EvaluateTestCases {

        MultiObjectiveKnapsackOld problem;

        @BeforeEach
        void setup() {
            int[] profitsA = {10, 5, 15, 7};
            int[] profitsB = {2, 6, 3, 4};
            var profits = List.of(profitsA, profitsB) ;
            int[] weights = {2, 3, 5, 7};
            int capacity = 8;

      problem = new MultiObjectiveKnapsackOld(profits, weights, capacity);
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

            BinarySolution solution = problem.createSolution();
            solution.variables().set(0, binarySet);

            // Act
            problem.evaluate(solution);

            // Assert
            int[] expectedObjectiveValues = {-1 * (25), -1 * 5};
            assertEquals(expectedObjectiveValues[0], solution.objectives()[0]);
            assertEquals(expectedObjectiveValues[1], solution.objectives()[1]);
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

            BinarySolution solution = problem.createSolution();
            solution.variables().set(0, binarySet);

            // Act
            problem.evaluate(solution);

            // Assert
            int expectedConstraintValue = -9;
            assertEquals(expectedConstraintValue, solution.constraints()[0]);
        }
    }
}
