package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem SpringDesignProblem (RCM15)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM15SpringDesignProblem extends AbstractDoubleProblem {

  private static final double[] WIRE_DIAMETER = {
      0.009, 0.0095, 0.0104, 0.0118, 0.0128, 0.0132, 0.014,
      0.015, 0.0162, 0.0173, 0.018, 0.020, 0.023, 0.025,
      0.028, 0.032, 0.035, 0.041, 0.047, 0.054, 0.063,
      0.072, 0.080, 0.092, 0.0105, 0.120, 0.135, 0.148,
      0.162, 0.177, 0.192, 0.207, 0.225, 0.244, 0.263,
      0.283, 0.307, 0.331, 0.362, 0.394, 0.4375, 0.500};

  public RCM15SpringDesignProblem() {
    numberOfObjectives(2);
    numberOfConstraints(8);
    name("SpringDesignProblem");

    List<Double> lowerLimit = Arrays.asList(0.51, 0.6, 0.51);
    List<Double> upperLimit = Arrays.asList(70.49, 3.0, 42.49);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = Math.round(solution.variables().get(0));
    double x2 = solution.variables().get(1);
    int index = Math.max(1, Math.min(42, (int) Math.round(solution.variables().get(2))));
    double x3 = WIRE_DIAMETER[index - 1];

    double cf = (4 * x2 / x3 - 1) / (4 * x2 / x3 - 4) + 0.615 * x3 / x2;
    double k = (11.5e6 * Math.pow(x3, 4)) / (8 * x1 * Math.pow(x2, 3));
    double lf = 1000 / k + 1.05 * (x1 + 2) * x3;
    double sigp = 300 / k;

    solution.objectives()[0] = (Math.PI * Math.PI * x2 * x3 * x3 * (x1 + 2)) / 4;
    solution.objectives()[1] = (8000 * cf * x2) / (Math.PI * Math.pow(x3, 3));

    double g1 = (8000 * cf * x2) / (Math.PI * Math.pow(x3, 3)) - 189000;
    double g2 = lf - 14;
    double g3 = 0.2 - x3;
    double g4 = x2 - 3;
    double g5 = 3 - x2 / x3;
    double g6 = sigp - 6;
    double g7 = sigp + 700 / k + 1.05 * (x1 + 2) * x3 - lf;
    double g8 = 1.25 - 700 / k;

    solution.constraints()[0] = -g1;
    solution.constraints()[1] = -g2;
    solution.constraints()[2] = -g3;
    solution.constraints()[3] = -g4;
    solution.constraints()[4] = -g5;
    solution.constraints()[5] = -g6;
    solution.constraints()[6] = -g7;
    solution.constraints()[7] = -g8;

    return solution;
  }
}
