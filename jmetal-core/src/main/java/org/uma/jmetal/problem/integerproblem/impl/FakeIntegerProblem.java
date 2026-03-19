package org.uma.jmetal.problem.integerproblem.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.integerproblem.IntegerProblem;
import org.uma.jmetal.solution.integersolution.IntegerSolution;

/**
 * Fake implementation of {@link IntegerProblem}. Intended to be used in unit tests.
 */
@SuppressWarnings("serial")
public class FakeIntegerProblem extends AbstractIntegerProblem {

  /**
   * Constructor with default bounds [0, 10] for all variables
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objectives
   * @param numberOfConstraints Number of constraints
   */
  public FakeIntegerProblem(int numberOfVariables, int numberOfObjectives,
      int numberOfConstraints) {
    this(numberOfVariables, numberOfObjectives, numberOfConstraints, 0, 10);
  }

  /**
   * Constructor with custom bounds for all variables
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objectives
   * @param numberOfConstraints Number of constraints
   * @param lowerBound Lower bound for all variables
   * @param upperBound Upper bound for all variables
   */
  public FakeIntegerProblem(int numberOfVariables, int numberOfObjectives,
      int numberOfConstraints, int lowerBound, int upperBound) {
    numberOfObjectives(numberOfObjectives);
    numberOfConstraints(numberOfConstraints);

    List<Integer> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Integer> upperLimit = new ArrayList<>(numberOfVariables);

    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(lowerBound);
      upperLimit.add(upperBound);
    }

    variableBounds(lowerLimit, upperLimit);
  }

  public FakeIntegerProblem() {
    this(2, 2, 0);
  }

  @Override
  public IntegerSolution evaluate(IntegerSolution solution) {
    return solution;
  }
}
