package org.uma.jmetal.solution.impl;

import org.uma.jmetal.problem.PermutationProblem;
import org.uma.jmetal.solution.PermutationSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines an implementation of solution composed of a permuation of integers
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class DefaultIntegerPermutationSolution
    extends AbstractGenericSolution<Integer, PermutationProblem<?>>
    implements PermutationSolution<Integer> {

  /** Constructor */
  public DefaultIntegerPermutationSolution(PermutationProblem<?> problem) {
    super(variablesInitializer(problem), problem.getNumberOfObjectives()) ;
  }

  private static List<Integer> variablesInitializer(PermutationProblem<?> problem) {
    List<Integer> randomSequence = new ArrayList<>(problem.getPermutationLength());

    for (int j = 0; j < problem.getPermutationLength(); j++) {
      randomSequence.add(j);
    }

    java.util.Collections.shuffle(randomSequence);

    return randomSequence ;
  }

  /** Copy Constructor */
  public DefaultIntegerPermutationSolution(DefaultIntegerPermutationSolution solution) {
    super(solution) ;
  }

  @Override public String getVariableValueString(int index) {
    return getVariableValue(index).toString();
  }

  @Override
  public DefaultIntegerPermutationSolution copy() {
    return new DefaultIntegerPermutationSolution(this);
  }
}
