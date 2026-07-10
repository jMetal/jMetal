package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem ProcessFlowSheetingProblem (RCM27)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 *
 * Note: x3 is not rounded to an integer in the reference MATLAB code for this problem, even though
 * its bounds ({@code [-0.49, 1.49]}) follow the usual integer-relaxation pattern used elsewhere in
 * the suite; this port keeps x3 continuous to match the reference evaluator exactly.
 */
public class RCM27ProcessFlowSheetingProblem extends AbstractDoubleProblem {

  public RCM27ProcessFlowSheetingProblem() {
    numberOfObjectives(2);
    numberOfConstraints(3);
    name("ProcessFlowSheetingProblem");

    List<Double> lowerLimit = Arrays.asList(0.2, -2.22554, -0.49);
    List<Double> upperLimit = Arrays.asList(1.0, -1.0, 1.49);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);

    solution.objectives()[0] = -0.7 * x3 + 0.8 + 5 * Math.pow(0.5 - x1, 2);
    solution.objectives()[1] = x1 - x3;

    double g1 = -(Math.exp(x1 - 0.2) + x2);
    double g2 = x2 + 1.1 * x3 - 1;
    double g3 = x1 - x3 - 0.2;

    solution.constraints()[0] = -g1;
    solution.constraints()[1] = -g2;
    solution.constraints()[2] = -g3;

    return solution;
  }
}
