package org.uma.jmetal.problem.doubleproblem.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Fake implementation of {@link DoubleProblem} that does nothing. Intended to be used in unit tests.
 */
@SuppressWarnings("serial")
public class FakeDoubleProblem extends AbstractDoubleProblem {
  /**
   * Constructor with default bounds [0.0, 1.0] for all variables
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objectives
   * @param numberOfConstraints Number of constraints
   */
  public FakeDoubleProblem(int numberOfVariables, int numberOfObjectives,
      int numberOfConstraints) {
    this(numberOfVariables, numberOfObjectives, numberOfConstraints, 0.0, 1.0);
  }

  /**
   * Constructor with custom bounds for all variables
   * @param numberOfVariables Number of variables
   * @param numberOfObjectives Number of objectives
   * @param numberOfConstraints Number of constraints
   * @param lowerBound Lower bound for all variables
   * @param upperBound Upper bound for all variables
   */
  public FakeDoubleProblem(int numberOfVariables, int numberOfObjectives,
      int numberOfConstraints, double lowerBound, double upperBound) {
    numberOfObjectives(numberOfObjectives);
    numberOfConstraints(numberOfConstraints);

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    List<Double> upperLimit = new ArrayList<>(numberOfVariables);

    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(lowerBound);
      upperLimit.add(upperBound);
    }

    variableBounds(lowerLimit, upperLimit);
  }

  public FakeDoubleProblem() {
    this(2, 2, 0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    return solution;
  }
}
