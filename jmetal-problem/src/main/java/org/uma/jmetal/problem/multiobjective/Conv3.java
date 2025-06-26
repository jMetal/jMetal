package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.Arrays;
import java.util.List;

/**
 * Class representing problem CONV3, a three-objective problem with 3 variables. Variables are
 * bounded in [-3.0, 3.0]
 */
public class Conv3 extends AbstractDoubleProblem {
  private final double[] a1 = {-1.0, -1.0, -1.0};
  private final double[] a2 = {1.0, 1.0, 1.0};
  private final double[] a3 = {-1.0, 1.0, -1.0};

  public Conv3() {
    numberOfObjectives(3);
    numberOfConstraints(0);
    name("CONV3");

    List<Double> lowerLimit = Arrays.asList(-3.0, -3.0, -3.0);
    List<Double> upperLimit = Arrays.asList(3.0, 3.0, 3.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = solution.variables().stream().mapToDouble(Double::doubleValue).toArray();

    double f1 = 0;
    double f2 = 0;
    double f3 = 0;
    for (int i = 0; i < 3; i++) {
      f1 += Math.pow(x[i] - a1[i], 2);
      f2 += Math.pow(x[i] - a2[i], 2);
      f3 += Math.pow(x[i] - a3[i], 2);
    }

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;
    solution.objectives()[2] = f3;

    return solution;
  }
}
