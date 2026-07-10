package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem CarSideImpactDesignProblem (RCM08)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM08CarSideImpactDesignProblem extends AbstractDoubleProblem {

  public RCM08CarSideImpactDesignProblem() {
    numberOfObjectives(3);
    numberOfConstraints(9);
    name("CarSideImpactDesignProblem");

    List<Double> lowerLimit = Arrays.asList(0.5, 0.45, 0.5, 0.5, 0.875, 0.4, 0.4);
    List<Double> upperLimit = Arrays.asList(1.5, 1.35, 1.5, 1.5, 2.625, 1.2, 1.2);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);
    double x4 = solution.variables().get(3);
    double x5 = solution.variables().get(4);
    double x6 = solution.variables().get(5);
    double x7 = solution.variables().get(6);

    double vmbp = 10.58 - 0.674 * x1 * x2 - 0.67275 * x2;
    double vfd = 16.45 - 0.489 * x3 * x7 - 0.843 * x5 * x6;

    double f2 = 4.72 - 0.5 * x4 - 0.19 * x2 * x3;

    solution.objectives()[0] = 1.98 + 4.9 * x1 * 6.67 * x2 + 6.98 * x3 + 4.01 * x4 + 1.78 * x5
        + 1e-5 * x6 + 2.73 * x7;
    solution.objectives()[1] = f2;
    solution.objectives()[2] = 0.5 * (vmbp + vfd);

    double g1 = -1 + 1.16 - 0.3717 * x2 * x4 - 0.0092928 * x3;
    double g2 = -0.32 + 0.261 - 0.0159 * x1 * x2 - 0.06486 * x1 - 0.019 * x2 * x7
        + 0.0144 * x2 * x5 + 0.0154464 * x6;
    double g3 = -0.32 + 0.74 - 0.61 * x2 - 0.031296 * x3 - 0.031872 * x7 + 0.227 * x2 * x2;
    double g4 = -0.32 + 0.214 + 0.00817 * x5 - 0.045195 * x1 - 0.0135168 * x1 + 0.03099 * x2 * x6
        - 0.018 * x2 * x7 + 0.007176 * x3 + 0.023232 * x3 - 0.00364 * x5 * x6 - 0.018 * x2 * x2;
    double g5 = -32 + 33.86 + 2.95 * x3 - 5.057 * x1 * x2 - 3.795 * x2 - 3.4431 * x7 + 1.45728;
    double g6 = -32 + 28.98 + 3.818 * x3 - 4.2 * x1 * x2 + 1.27296 * x6 - 2.68065 * x7;
    double g7 = -32 + 46.36 - 9.9 * x2 - 4.4505 * x1;
    double g8 = f2 - 4;
    double g9 = vmbp - 9.9;

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
