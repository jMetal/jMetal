package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem DiscBrakeDesignProblem (RCM05)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM05DiscBrakeDesignProblem extends AbstractDoubleProblem {

  public RCM05DiscBrakeDesignProblem() {
    numberOfObjectives(2);
    numberOfConstraints(4);
    name("DiscBrakeDesignProblem");

    List<Double> lowerLimit = Arrays.asList(55.0, 75.0, 1000.0, 11.0);
    List<Double> upperLimit = Arrays.asList(80.0, 110.0, 3000.0, 20.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);
    double x4 = solution.variables().get(3);

    solution.objectives()[0] = 4.9e-5 * (x2 * x2 - x1 * x1) * (x4 - 1);
    solution.objectives()[1] =
        9.82e6 * (x2 * x2 - x1 * x1) / (x3 * x4 * (Math.pow(x2, 3) - Math.pow(x1, 3)));

    double g1 = 20 - (x2 - x1);
    double g2 = x3 / (3.14 * (x2 * x2 - x1 * x1)) - 0.4;
    double g3 = 2.22e-3 * x3 * (Math.pow(x2, 3) - Math.pow(x1, 3)) / Math.pow(x2 * x2 - x1 * x1, 2)
        - 1;
    double g4 = 900 - 2.66e-2 * x3 * x4 * (Math.pow(x2, 3) - Math.pow(x1, 3)) / (x2 * x2 - x1 * x1);

    solution.constraints()[0] = -g1;
    solution.constraints()[1] = -g2;
    solution.constraints()[2] = -g3;
    solution.constraints()[3] = -g4;

    return solution;
  }
}
