package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem ProcessSynthesisProblem2 (RCM29)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM29ProcessSynthesisProblem2 extends AbstractDoubleProblem {

  public RCM29ProcessSynthesisProblem2() {
    numberOfObjectives(2);
    numberOfConstraints(9);
    name("ProcessSynthesisProblem2");

    List<Double> lowerLimit = Arrays.asList(0.0, 0.0, 0.0, -0.49, -0.49, -0.49, -0.49);
    List<Double> upperLimit = Arrays.asList(100.0, 100.0, 100.0, 1.49, 1.49, 1.49, 1.49);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);
    double x4 = Math.round(solution.variables().get(3));
    double x5 = Math.round(solution.variables().get(4));
    double x6 = Math.round(solution.variables().get(5));
    double x7 = Math.round(solution.variables().get(6));

    solution.objectives()[0] = Math.pow(1 - x4, 2) + Math.pow(1 - x5, 2) + Math.pow(1 - x6, 2)
        - Math.log(Math.abs(1 + x7) + 1e-6);
    solution.objectives()[1] = Math.pow(1 - x1, 2) + Math.pow(2 - x2, 2) + Math.pow(3 - x3, 2);

    double g1 = x1 + x2 + x3 + x4 + x5 + x6 - 5;
    double g2 = Math.pow(x6, 3) + x1 * x1 + x2 * x2 + x3 * x3 - 5.5;
    double g3 = x1 + x4 - 1.2;
    double g4 = x2 + x5 - 1.8;
    double g5 = x3 + x6 - 2.5;
    double g6 = x1 + x7 - 1.2;
    double g7 = x5 * x5 + x2 * x2 - 1.64;
    double g8 = x6 * x6 + x3 * x3 - 4.25;
    double g9 = x5 * x5 + x3 * x3 - 4.64;

    solution.constraints()[0] = -g1;
    solution.constraints()[1] = -g2;
    solution.constraints()[2] = -g3;
    solution.constraints()[3] = -g4;
    solution.constraints()[4] = -g5;
    solution.constraints()[5] = -g6;
    solution.constraints()[6] = -g7;
    solution.constraints()[7] = -g8;
    solution.constraints()[8] = -g9;

    return solution;
  }
}
