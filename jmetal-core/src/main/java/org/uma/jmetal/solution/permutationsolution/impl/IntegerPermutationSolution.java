package org.uma.jmetal.solution.permutationsolution.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

/**
 * Defines an implementation of solution composed of a permutation of integers. A permutation is
 * represented as a list of integers, so the permutation length is equal to the number of variables.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class IntegerPermutationSolution extends AbstractSolution<Integer>
        implements PermutationSolution<Integer> {
  /**
   * Constructor
   */
  public IntegerPermutationSolution(int permutationLength, int numberOfObjectives) {
    super(permutationLength, numberOfObjectives);

    List<Integer> randomSequence = IntStream.range(0, permutationLength).boxed().collect(Collectors.toCollection(() -> new ArrayList<>(permutationLength)));

      java.util.Collections.shuffle(randomSequence);

    IntStream.range(0, permutationLength).forEach(i -> variables().set(i, randomSequence.get(i)));
  }

  /**
   * Copy Constructor
   */
  public IntegerPermutationSolution(IntegerPermutationSolution solution) {
    super(solution.getLength(), solution.objectives().length);

    Arrays.setAll(objectives(), i -> solution.objectives()[i]);

    for (int i = 0; i < variables().size(); i++) {
      variables().set(i, solution.variables().get(i));
    }

    Arrays.setAll(constraints(), i -> solution.constraints()[i]);

    attributes = new HashMap<Object, Object>(solution.attributes);
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
