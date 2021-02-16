package org.uma.jmetal.solution.permutationsolution.impl;

import org.uma.jmetal.solution.AbstractSolution;
import org.uma.jmetal.solution.permutationsolution.PermutationSolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines an implementation of solution composed of a permutation of integers. A permutation is
 * represented as a list of integers, so the permutation length is equal to the number of variables.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class IntegerPermutationSolution extends AbstractSolution<Integer>
    implements PermutationSolution<Integer> {

  /** Constructor */
  public IntegerPermutationSolution(int permutationLength, int numberOfObjectives) {
    super(permutationLength, numberOfObjectives);

    List<Integer> randomSequence = new ArrayList<>(permutationLength);

    for (int j = 0; j < permutationLength; j++) {
      randomSequence.add(j);
    }

    java.util.Collections.shuffle(randomSequence);

    for (int i = 0; i < permutationLength; i++) {
      setVariable(i, randomSequence.get(i));
    }
  }

  /** Copy Constructor */
  public IntegerPermutationSolution(IntegerPermutationSolution solution) {
    super(solution.getLength(), solution.objectives().length);

    for (int i = 0; i < objectives().length; i++) {
      setObjective(i, solution.objectives()[i]) ;
    }

    for (int i = 0; i < variables().size(); i++) {
      setVariable(i, solution.getVariable(i));
    }

    for (int i = 0; i < constraints().length; i++) {
      constraints()[i] =  solution.constraints()[i];
    }

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
