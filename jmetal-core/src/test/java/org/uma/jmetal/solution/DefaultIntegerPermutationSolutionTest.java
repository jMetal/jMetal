package org.uma.jmetal.solution;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.permutationproblem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.problem.permutationproblem.impl.FakeIntegerPermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.solution.permutationsolution.impl.IntegerPermutationSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/** @author Antonio J. Nebro  */
public class DefaultIntegerPermutationSolutionTest {

  @Test
  public void shouldConstructorCreateAValidSolution() {
    int permutationLength = 20;
    AbstractIntegerPermutationProblem problem =
        new FakeIntegerPermutationProblem(permutationLength, 2);
    PermutationSolution<Integer> solution = problem.createSolution();

    List<Integer> values = new ArrayList<>();
    for (int i = 0; i < problem.numberOfVariables(); i++) {
      values.add(solution.variables().get(i));
    }

    Collections.sort(values);

    List<Integer> expectedList = new ArrayList<>(permutationLength);
    for (int i = 0; i < permutationLength; i++) {
      expectedList.add(i);
    }

    assertArrayEquals(expectedList.toArray(), values.toArray());
  }

  @Test
  public void givenTheSameSeed_whenCreatingTwoSolutions_thenThePermutationsAreEqual() {
    JMetalRandom.getInstance().setSeed(1);
    List<Integer> first = new IntegerPermutationSolution(20, 2, 0).variables();
    JMetalRandom.getInstance().setSeed(1);
    List<Integer> second = new IntegerPermutationSolution(20, 2, 0).variables();

    assertEquals(first, second);
  }

}
