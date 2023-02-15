package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem PressureVesselProblem (RCM01)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */

public class RCM01PressureVesselProblem extends AbstractDoubleProblem {

  public RCM01PressureVesselProblem() {
    numberOfObjectives(2);
    numberOfConstraints(2);
    name("PressureVesselProblem");

    List<Double> lowerLimit = Arrays.asList(1.0, 1.0, 10.0, 10.0);
    List<Double> upperLimit = Arrays.asList(99.0, 99.0, 200.0, 200.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    int x1 = (int) Math.round(solution.variables().get(0));
    int x2 = (int) Math.round(solution.variables().get(1));
    double x3 = solution.variables().get(2);
    double x4 = solution.variables().get(3);
    double z1 = 0.0625 * x1;
    double z2 = 0.0625 * x2;

    solution.objectives()[0] =
        1.7781 * z1 * Math.pow(x3, 2) + 0.6224 * z1 * x2 * x4 + 3.1661 * Math.pow(z1, 2) * x4
            + 19.84 * Math.pow(z1, 2) * x3;
    solution.objectives()[1] =
        -Math.PI * Math.pow(x3, 2) * x4 - (4.0 / 3.0) * Math.PI * Math.pow(x3, 3);

    solution.constraints()[0] = 0.00954 * x3 - z2;
    solution.constraints()[1] = 0.0193 * x3 - z1;

    return solution;
  }
}
