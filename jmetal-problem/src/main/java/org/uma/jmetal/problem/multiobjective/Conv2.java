package org.uma.jmetal.problem.multiobjective;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem CONV2, a bi-objective problem with 2 variables.
 *
 * <p>Problem definition: Minimize f1(x) = x1² + x2² Minimize f2(x) = (x1 - 10)² + x2² where x1, x2
 * ∈ [0.0, 10.0]
 */
public class Conv2 extends AbstractDoubleProblem {

  /** Constructor */
  public Conv2() {
    numberOfObjectives(2);
    numberOfConstraints(0);
    name("CONV2");

    List<Double> lowerLimit = Arrays.asList(0.0, 0.0);
    List<Double> upperLimit = Arrays.asList(10.0, 10.0);

    variableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);

    double f1 = x1 * x1 + x2 * x2;
    double f2 = Math.pow(x1 - 10.0, 2) + x2 * x2;

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;

    return solution;
  }
}
