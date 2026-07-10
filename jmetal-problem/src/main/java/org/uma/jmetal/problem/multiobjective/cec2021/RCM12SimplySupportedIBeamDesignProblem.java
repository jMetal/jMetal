package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem SimplySupportedIBeamDesignProblem (RCM12)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM12SimplySupportedIBeamDesignProblem extends AbstractDoubleProblem {

  public RCM12SimplySupportedIBeamDesignProblem() {
    numberOfObjectives(2);
    numberOfConstraints(1);
    name("SimplySupportedIBeamDesignProblem");

    List<Double> lowerLimit = Arrays.asList(10.0, 10.0, 0.9, 0.9);
    List<Double> upperLimit = Arrays.asList(80.0, 50.0, 5.0, 5.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);
    double x4 = solution.variables().get(3);

    double p = 600;
    double l = 200;
    double e = 2e4;

    solution.objectives()[0] = 2 * x2 * x4 + x3 * (x1 - 2 * x4);
    solution.objectives()[1] = p * Math.pow(l, 3) / (48 * e
        * (x3 * Math.pow(x1 - 2 * x4, 3) + 2 * x2 * x4 * (4 * x4 * x4 + 3 * x1 * (x1 - 2 * x4)))
        / 12);

    double g1 = -16
        + 180000 * x1 / (x3 * Math.pow(x1 - 2 * x4, 3) + 2 * x2 * x4 * (4 * x4 * x4 + 3 * x1 * (x1
            - 2 * x4)))
        + 15000 * x2 / ((x1 - 2 * x4) * Math.pow(x3, 3) + 2 * x4 * Math.pow(x2, 3));

    solution.constraints()[0] = -g1;

    return solution;
  }
}
