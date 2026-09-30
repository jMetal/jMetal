package org.uma.jmetal.operator.mutation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.mutation.impl.ScrambleMutation;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

@DisplayName("Unit tests for class ScrambleMutation")
class ScrambleMutationTest {

  private ScrambleMutation<Integer> mutation;

  @BeforeEach
  void setUp() {
    mutation = new ScrambleMutation<>(1.0);
  }

  @Nested
  @DisplayName("When mutating a permutation")
  class WhenMutatingAPermutation {

    @Test
    @DisplayName("given the same seed, when mutating twice, then the results are equal")
    void givenTheSameSeed_whenMutatingTwice_thenTheResultsAreEqual() {
      // Act
      List<List<Integer>> results = new ArrayList<>();
      for (int run = 0; run < 2; run++) {
        JMetalRandom.getInstance().setSeed(1);
        List<Integer> mutated = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
          mutated.addAll(mutation.execute(identity(20)).variables());
        }
        results.add(mutated);
      }

      // Assert
      assertEquals(results.get(0), results.get(1));
    }
  }

  private static IntegerPermutationSolution identity(int length) {
    IntegerPermutationSolution solution = new IntegerPermutationSolution(length, 1, 0);
    for (int i = 0; i < length; i++) {
      solution.variables().set(i, i);
    }
    return solution;
  }
}
