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

      assertArrayEquals(IntStream.range(0, permutationLength).boxed().toArray(), IntStream.range(0, problem.getNumberOfVariables()).mapToObj(i -> solution.variables().get(i)).sorted().toArray());
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
