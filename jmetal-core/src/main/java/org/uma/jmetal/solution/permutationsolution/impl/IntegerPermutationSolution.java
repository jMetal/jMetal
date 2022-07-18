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

      List<Integer> randomSequence = new ArrayList<>(permutationLength);
      for (int i1 = 0; i1 < permutationLength; i1++) {
          Integer integer = i1;
          randomSequence.add(integer);
      }

      java.util.Collections.shuffle(randomSequence);

      for (int i = 0; i < permutationLength; i++) {
          variables().set(i, randomSequence.get(i));
      }
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
