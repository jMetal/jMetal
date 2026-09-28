package org.uma.jmetal.solution.permutationsolution.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Defines an implementation of solution composed of a permutation of integers. A permutation is
 * represented as a list of integers, so the permutation length is equal to the number of variables.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class IntegerPermutationSolution extends AbstractSolution<Integer>
        implements PermutationSolution<Integer> {
  /**
   * Constructor
   */
  public IntegerPermutationSolution(int permutationLength, int numberOfObjectives, int numberOfConstraints) {
    super(permutationLength, numberOfObjectives, numberOfConstraints);

    List<Integer> randomSequence = new ArrayList<>(permutationLength);

    for (int j = 0; j < permutationLength; j++) {
      randomSequence.add(j);
    }

    // Fisher-Yates shuffle with JMetalRandom, so that the permutation depends on its seed
    JMetalRandom random = JMetalRandom.getInstance();
    for (int j = permutationLength - 1; j > 0; j--) {
      Collections.swap(randomSequence, j, random.nextInt(0, j));
    }

    IntStream.range(0, permutationLength).forEach(i -> variables().set(i, randomSequence.get(i)));
  }

  /**
   * Copy Constructor
   */
  public IntegerPermutationSolution(IntegerPermutationSolution solution) {
    super(solution.getLength(), solution.objectives().length, solution.constraints().length);

    IntStream.range(0, solution.variables().size()).forEach(i -> variables().set(i, solution.variables().get(i)));
    IntStream.range(0, solution.objectives().length).forEach(i -> objectives()[i] = solution.objectives()[i]);
    IntStream.range(0, solution.constraints().length).forEach(i -> constraints()[i] = solution.constraints()[i]);

    attributes = new HashMap<>(solution.attributes);
  }

  @Override
  public IntegerPermutationSolution copy() {
    return new IntegerPermutationSolution(this);
  }

  @Override
  public int getLength() {
    return variables().size();
  }
}
