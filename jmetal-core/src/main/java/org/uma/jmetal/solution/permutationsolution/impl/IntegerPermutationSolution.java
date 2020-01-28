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
    super(solution.getLength(), solution.getNumberOfObjectives());

    for (int i = 0; i < getNumberOfObjectives(); i++) {
      setObjective(i, solution.getObjective(i));
    }

    for (int i = 0; i < getNumberOfVariables(); i++) {
      setVariable(i, solution.getVariable(i));
    }

    for (int i = 0; i < getNumberOfConstraints(); i++) {
      setConstraint(i, solution.getConstraint(i));
    }

    attributes = new HashMap<Object, Object>(solution.attributes);
  }

  @Override
  public IntegerPermutationSolution copy() {
    return new IntegerPermutationSolution(this);
  }

  @Override
  public Map<Object, Object> getAttributes() {
    return attributes;
  }

  @Override
  public int getLength() {
    return getNumberOfVariables();
  }
}
