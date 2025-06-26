package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.Arrays;
import java.util.List;

/**
 * Class representing problem CONV3_4, a three-objective problem with 3 variables. Variables are
 * bounded in [-3.0, 3.0]
 */
public class Conv3_4 extends AbstractDoubleProblem {
  private final double[] a1 = {-1.0, -1.0, -1.0};
  private final double[] a2 = {1.0, 1.0, 1.0};
  private final double[] a3 = {-1.0, 1.0, -1.0};

  public Conv3_4() {
    numberOfObjectives(3);
    numberOfConstraints(0);
    name("CONV3_4");

    List<Double> lowerLimit = Arrays.asList(-3.0, -3.0, -3.0);
    List<Double> upperLimit = Arrays.asList(3.0, 3.0, 3.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = solution.variables().stream().mapToDouble(Double::doubleValue).toArray();

    double f1 = Math.pow(x[0] - a1[0], 4) + Math.pow(x[1] - a1[1], 2) + Math.pow(x[2] - a1[2], 2);
    double f2 = Math.pow(x[0] - a2[0], 2) + Math.pow(x[1] - a2[1], 4) + Math.pow(x[2] - a2[2], 2);
    double f3 = Math.pow(x[0] - a3[0], 2) + Math.pow(x[1] - a3[1], 2) + Math.pow(x[2] - a3[2], 4);

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.objectives()[2] = f3;

    return solution;
  }
}
