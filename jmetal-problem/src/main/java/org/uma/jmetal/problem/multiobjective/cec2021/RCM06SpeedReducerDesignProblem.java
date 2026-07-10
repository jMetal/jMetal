package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem SpeedReducerDesignProblem (RCM06)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM06SpeedReducerDesignProblem extends AbstractDoubleProblem {

  public RCM06SpeedReducerDesignProblem() {
    numberOfObjectives(2);
    numberOfConstraints(11);
    name("SpeedReducerDesignProblem");

    List<Double> lowerLimit = Arrays.asList(2.6, 0.7, 16.51, 7.3, 7.3, 2.9, 5.0);
    List<Double> upperLimit = Arrays.asList(3.6, 0.8, 28.49, 8.3, 8.3, 3.9, 5.5);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = Math.round(solution.variables().get(2));
    double x4 = solution.variables().get(3);
    double x5 = solution.variables().get(4);
    double x6 = solution.variables().get(5);
    double x7 = solution.variables().get(6);

    double f1 = 0.7854 * x1 * x2 * x2 * (10 * x3 * x3 / 3 + 14.933 * x3 - 43.0934)
        - 1.508 * x1 * (x6 * x6 + x7 * x7)
        + 7.477 * (Math.pow(x6, 3) + Math.pow(x7, 3))
        + 0.7854 * (x4 * x6 * x6 + x5 * x7 * x7);
    double f2 = Math.sqrt(Math.pow(745 * x4 / (x2 * x3), 2) + 1.69e7) / (0.1 * Math.pow(x6, 3));

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;

    double g1 = 1 / (x1 * x2 * x2 * x3) - 1.0 / 27;
    double g2 = 1 / (x1 * x2 * x2 * x3 * x3) - 1.0 / 397.5;
    double g3 = Math.pow(x4, 3) / (x2 * x3 * Math.pow(x6, 4)) - 1.0 / 1.93;
    double g4 = Math.pow(x5, 3) / (x2 * x3 * Math.pow(x7, 4)) - 1.0 / 1.93;
    double g5 = x2 * x3 - 40;
    double g6 = x1 / x2 - 12;
    double g7 = -x1 / x2 + 5;
    double g8 = 1.9 - x4 + 1.5 * x6;
    double g9 = 1.9 - x5 + 1.1 * x7;
    double g10 = f2 - 1300;
    double g11 = Math.sqrt(Math.pow(745 * x5 / (x2 * x3), 2) + 1.575e8) / (0.1 * Math.pow(x7, 3))
        - 850;

    solution.constraints()[0] = -g1;
    solution.constraints()[1] = -g2;
    solution.constraints()[2] = -g3;
    solution.constraints()[3] = -g4;
    solution.constraints()[4] = -g5;
    solution.constraints()[5] = -g6;
    solution.constraints()[6] = -g7;
    solution.constraints()[7] = -g8;
    solution.constraints()[8] = -g9;
    solution.constraints()[9] = -g10;
    solution.constraints()[10] = -g11;

    return solution;
  }
}
