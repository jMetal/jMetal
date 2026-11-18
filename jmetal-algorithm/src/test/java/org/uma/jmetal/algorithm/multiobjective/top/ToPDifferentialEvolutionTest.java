package org.uma.jmetal.algorithm.multiobjective.top;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

@DisplayName("Unit tests for class ToPDifferentialEvolution")
class ToPDifferentialEvolutionTest {
  private static final double EPSILON = 1e-12;
  private static final double F = 0.5;

  /** A solution with variables in [0, 1]. */
  private static DoubleSolution solution(double... variables) {
    DoubleSolution solution =
        new DefaultDoubleSolution(
            Collections.nCopies(variables.length, Bounds.create(0.0, 1.0)), 2, 0);
    for (int i = 0; i < variables.length; i++) {
      solution.variables().set(i, variables[i]);
    }
    return solution;
  }

  /** A generator that returns the given values cyclically. */
  private static RandomGenerator<Double> sequence(double... values) {
    int[] next = {0};
    return () -> values[next[0]++ % values.length];
  }

  /** The operators with no mutation and a crossover that always takes the mutant vector. */
  private static ToPDifferentialEvolution operatorsWithoutMutation() {
    return new ToPDifferentialEvolution(1.0, F, new PolynomialMutation(0.0, 20.0), sequence(0.0));
  }

  private static void assertVariables(DoubleSolution solution, double... expected) {
    assertEquals(expected.length, solution.variables().size());
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], solution.variables().get(i), EPSILON, "variable " + i);
    }
  }

  /** A mutation that records the variables it receives and sets the first one to 0.25. */
  private static class RecordingMutation implements MutationOperator<DoubleSolution> {
    private final List<List<Double>> received = new ArrayList<>();

    @Override
    public DoubleSolution execute(DoubleSolution solution) {
      received.add(new ArrayList<>(solution.variables()));
      solution.variables().set(0, 0.25);
      return solution;
    }

    @Override
    public double mutationProbability() {
      return 1.0;
    }
  }

  @Nested
  @DisplayName("When the crossover rate is one")
  class WhenTheCrossoverRateIsOne {
    @Test
    @DisplayName("given four parents, when DE/current-to-rand/1, then x + F(r1 - x) + F(r2 - r3)")
    void givenFourParents_whenCurrentToRand1_thenTheFormulaOfPlatEMOIsApplied() {
      // Arrange
      DoubleSolution current = solution(0.2, 0.4);
      DoubleSolution r1 = solution(0.6, 0.8);
      DoubleSolution r2 = solution(0.5, 0.5);
      DoubleSolution r3 = solution(0.1, 0.3);

      // Act
      DoubleSolution trial = operatorsWithoutMutation().currentToRand1(current, r1, r2, r3);

      // Assert
      assertVariables(trial, 0.6, 0.7);
    }

    @Test
    @DisplayName("given four parents, when DE/rand-to-best/1, then r1 + F(best - r1) + F(r2 - r3)")
    void givenFourParents_whenRandToBest1_thenTheFormulaOfPlatEMOIsApplied() {
      // Arrange
      DoubleSolution best = solution(0.9, 0.1);
      DoubleSolution r1 = solution(0.3, 0.5);
      DoubleSolution r2 = solution(0.6, 0.4);
      DoubleSolution r3 = solution(0.2, 0.2);

      // Act
      DoubleSolution trial = operatorsWithoutMutation().randToBest1(best, r1, r2, r3);

      // Assert
      assertVariables(trial, 0.8, 0.4);
    }

    @Test
    @DisplayName("given three parents, when DE/rand/1, then x1 + F(x2 - x3)")
    void givenThreeParents_whenRand1_thenTheFormulaOfPlatEMOIsApplied() {
      // Arrange
      DoubleSolution base = solution(0.5, 0.5);
      DoubleSolution second = solution(0.8, 0.2);
      DoubleSolution third = solution(0.2, 0.4);

      // Act
      DoubleSolution trial = operatorsWithoutMutation().rand1(base, second, third);

      // Assert
      assertVariables(trial, 0.8, 0.4);
    }

    @Test
    @DisplayName("given the same solution as every parent, when DE/current-to-rand/1, then a copy")
    void givenTheSameSolutionAsEveryParent_whenCurrentToRand1_thenTheTrialVectorIsACopy() {
      // Arrange: as in PlatEMO, the random indices can repeat and be equal to i
      DoubleSolution current = solution(0.3, 0.7);

      // Act
      DoubleSolution trial =
          operatorsWithoutMutation().currentToRand1(current, current, current, current);

      // Assert
      assertNotSame(current, trial);
      assertVariables(trial, 0.3, 0.7);
    }

    @Test
    @DisplayName("given parents, when creating a trial vector, then the parents are not modified")
    void givenParents_whenCreatingATrialVector_thenTheParentsAreNotModified() {
      // Arrange
      DoubleSolution best = solution(0.9, 0.1);
      DoubleSolution r1 = solution(0.3, 0.5);
      DoubleSolution r2 = solution(0.6, 0.4);
      DoubleSolution r3 = solution(0.2, 0.2);

      // Act
      operatorsWithoutMutation().randToBest1(best, r1, r2, r3);

      // Assert
      assertVariables(best, 0.9, 0.1);
      assertVariables(r1, 0.3, 0.5);
      assertVariables(r2, 0.6, 0.4);
      assertVariables(r3, 0.2, 0.2);
    }
  }

  @Nested
  @DisplayName("When the crossover rate is lower than one")
  class WhenTheCrossoverRateIsLowerThanOne {
    @Test
    @DisplayName("given a draw over CR, when DE/current-to-rand/1, then the variable is x_i's")
    void givenADrawOverCr_whenCurrentToRand1_thenTheVariableIsTheOneOfTheCurrentSolution() {
      // Arrange: 0.1 < CR crosses the first variable, 0.9 >= CR keeps the second one
      var operators =
          new ToPDifferentialEvolution(
              0.5, F, new PolynomialMutation(0.0, 20.0), sequence(0.1, 0.9));
      DoubleSolution current = solution(0.2, 0.4);

      // Act
      DoubleSolution trial =
          operators.currentToRand1(
              current, solution(0.6, 0.8), solution(0.5, 0.5), solution(0.1, 0.3));

      // Assert
      assertVariables(trial, 0.6, 0.4);
    }

    @Test
    @DisplayName("given a draw over CR, when DE/rand-to-best/1, then the variable is r1's")
    void givenADrawOverCr_whenRandToBest1_thenTheVariableIsTheOneOfR1() {
      // Arrange: PlatEMO takes the variables that are not crossed from x_r1, not from x_i
      var operators =
          new ToPDifferentialEvolution(
              0.5, F, new PolynomialMutation(0.0, 20.0), sequence(0.1, 0.9));
      DoubleSolution r1 = solution(0.3, 0.5);

      // Act
      DoubleSolution trial =
          operators.randToBest1(solution(0.9, 0.1), r1, solution(0.6, 0.4), solution(0.2, 0.2));

      // Assert
      assertVariables(trial, 0.8, 0.5);
    }

    @Test
    @DisplayName("given draws equal to CR, when DE/rand/1, then no variable is crossed")
    void givenDrawsEqualToCr_whenRand1_thenNoVariableIsCrossed() {
      // Arrange: a variable is crossed when the draw is strictly lower than CR
      var operators =
          new ToPDifferentialEvolution(0.5, F, new PolynomialMutation(0.0, 20.0), sequence(0.5));

      // Act
      DoubleSolution trial =
          operators.rand1(solution(0.5, 0.5), solution(0.8, 0.2), solution(0.2, 0.4));

      // Assert
      assertVariables(trial, 0.5, 0.5);
    }
  }

  @Nested
  @DisplayName("When the mutant vector is out of the bounds")
  class WhenTheMutantVectorIsOutOfTheBounds {
    @Test
    @DisplayName("given a mutant out of the bounds, when creating it, then it is clamped")
    void givenAMutantOutOfTheBounds_whenCreatingTheTrialVector_thenItIsClampedToTheBounds() {
      // Arrange: 0.9 + 0.5 (1 - 0.9) + 0.5 (1 - 0) = 1.45 and 0.1 + 0.5 (0 - 0.1) + 0.5 (0 - 1)
      DoubleSolution current = solution(0.9, 0.1);

      // Act
      DoubleSolution trial =
          operatorsWithoutMutation()
              .currentToRand1(current, solution(1.0, 0.0), solution(1.0, 0.0), solution(0.0, 1.0));

      // Assert
      assertVariables(trial, 1.0, 0.0);
    }

    @Test
    @DisplayName("given a mutation, when creating a trial vector, then it mutates the clamped one")
    void givenAMutation_whenCreatingATrialVector_thenItMutatesTheClampedVector() {
      // Arrange
      RecordingMutation mutation = new RecordingMutation();
      var operators = new ToPDifferentialEvolution(1.0, F, mutation, sequence(0.0));
      DoubleSolution base = solution(0.9, 0.1);

      // Act: 0.9 + 0.5 (1 - 0) = 1.4 and 0.1 + 0.5 (0 - 1) = -0.4
      DoubleSolution trial = operators.rand1(base, solution(1.0, 0.0), solution(0.0, 1.0));

      // Assert
      assertEquals(List.of(List.of(1.0, 0.0)), mutation.received);
      assertVariables(trial, 0.25, 0.0);
    }
  }
}
