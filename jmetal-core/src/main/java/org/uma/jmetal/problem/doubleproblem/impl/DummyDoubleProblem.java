package org.uma.jmetal.problem.doubleproblem.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Implementation of {@link DoubleProblem} that does nothing. Intended to be used in unit tests.
 */
@SuppressWarnings("serial")
public class DummyDoubleProblem extends AbstractDoubleProblem {

  public DummyDoubleProblem(int numberOfVariables, int numberOfObjectives,
      int numberOfConstraints) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(numberOfConstraints);

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  public DummyDoubleProblem() {
    this(2, 2, 0);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    return solution;
  }
}
