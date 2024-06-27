package org.uma.jmetal.solution;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.uma.jmetal.problem.permutationproblem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.problem.permutationproblem.impl.FakeIntegerPermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

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

}
