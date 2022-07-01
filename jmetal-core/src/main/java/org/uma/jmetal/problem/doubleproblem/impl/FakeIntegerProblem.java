package org.uma.jmetal.problem.doubleproblem.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.integerproblem.impl.AbstractIntegerProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

/**
 * Implementation of {@link org.uma.jmetal.problem.integerproblem.IntegerProblem} that does nothing. Intended to be used in unit tests.
 */
@SuppressWarnings("serial")
public class FakeIntegerProblem extends AbstractIntegerProblem {

  public FakeIntegerProblem(int numberOfVariables, int numberOfObjectives,
      int numberOfConstraints) {
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(numberOfConstraints);

    List<Integer> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Integer> upperLimit = new ArrayList<>(numberOfVariables);

    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(0);
      upperLimit.add(10);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  public FakeIntegerProblem() {
    this(2, 2, 0);
  }

  @Override
  public IntegerSolution evaluate(IntegerSolution solution) {
    return solution;
  }
}
