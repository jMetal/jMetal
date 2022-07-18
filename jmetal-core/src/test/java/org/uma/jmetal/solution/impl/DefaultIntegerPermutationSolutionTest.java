package org.uma.jmetal.solution.impl;

import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;
import org.uma.jmetal.problem.permutationproblem.impl.AbstractIntegerPermutationProblem;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

/** @author Antonio J. Nebro <antonio@lcc.uma.es> */
public class DefaultIntegerPermutationSolutionTest {

  @Test
  public void shouldConstructorCreateAValidSolution() {
    int permutationLength = 20;
    AbstractIntegerPermutationProblem problem =
        new MockIntegerPermutationProblem(permutationLength);
    PermutationSolution<Integer> solution = problem.createSolution();

    List<Integer> list = new ArrayList<>();
    int bound = problem.getNumberOfVariables();
    for (int i = 0; i < bound; i++) {
      Integer integer = solution.variables().get(i);
      list.add(integer);
    }
    list.sort(null);
    List<Integer> result = new ArrayList<>();
    for (int i = 0; i < permutationLength; i++) {
      Integer integer = i;
      result.add(integer);
    }
    assertArrayEquals(result.toArray(), list.toArray());
  }

  /** Mock class representing a integer permutation problem */
  @SuppressWarnings("serial")
  private class MockIntegerPermutationProblem extends AbstractIntegerPermutationProblem {

    /** Constructor */
    public MockIntegerPermutationProblem(int permutationLength) {
      setNumberOfVariables(permutationLength);
      setNumberOfObjectives(2);
    }

    @Override
    public PermutationSolution<Integer> evaluate(PermutationSolution<Integer> solution) {
      return solution;
    }

    @Override
    public int getLength() {
      return getNumberOfVariables();
    }
  }
}
