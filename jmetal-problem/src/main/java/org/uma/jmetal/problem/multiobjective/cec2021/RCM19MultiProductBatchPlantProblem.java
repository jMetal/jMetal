package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MultiProductBatchPlantProblem (RCM19)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM19MultiProductBatchPlantProblem extends AbstractDoubleProblem {

  public RCM19MultiProductBatchPlantProblem() {
    numberOfObjectives(3);
    numberOfConstraints(10);
    name("MultiProductBatchPlantProblem");

    List<Double> lowerLimit =
        Arrays.asList(0.51, 0.51, 0.51, 250.0, 250.0, 250.0, 6.0, 4.0, 40.0, 10.0);
    List<Double> upperLimit =
        Arrays.asList(3.49, 3.49, 3.49, 2500.0, 2500.0, 2500.0, 20.0, 16.0, 700.0, 450.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double n1 = Math.round(solution.variables().get(0));
    double n2 = Math.round(solution.variables().get(1));
    double n3 = Math.round(solution.variables().get(2));
    double v1 = solution.variables().get(3);
    double v2 = solution.variables().get(4);
    double v3 = solution.variables().get(5);
    double tl1 = solution.variables().get(6);
    double tl2 = solution.variables().get(7);
    double b1 = solution.variables().get(8);
    double b2 = solution.variables().get(9);

    double s11 = 2, s21 = 4;
    double s12 = 3, s22 = 6;
    double s13 = 4, s23 = 3;
    double t11 = 8, t21 = 16;
    double t12 = 20, t22 = 4;
    double t13 = 8, t23 = 4;
    double h = 6000;
    double alp = 250;
    double beta = 0.6;
    double q1 = 40000;
    double q2 = 20000;

    solution.objectives()[0] =
        alp * (n1 * Math.pow(v1, beta) + n2 * Math.pow(v2, beta) + n3 * Math.pow(v3, beta));
    solution.objectives()[1] = 65 * (q1 / b1 + q2 / b2) + 0.08 * q1 + 0.1 * q2;
    solution.objectives()[2] = q1 * tl1 / b1 + q2 * tl2 / b2;

    double g1 = q1 * tl1 / b1 + q2 * tl2 / b2 - h;
    double g2 = s11 * b1 + s21 * b2 - v1;
    double g3 = s12 * b1 + s22 * b2 - v2;
    double g4 = s13 * b1 + s23 * b2 - v3;
    double g5 = t11 - n1 * tl1;
    double g6 = t12 - n2 * tl1;
    double g7 = t13 - n3 * tl1;
    double g8 = t21 - n1 * tl2;
    double g9 = t22 - n2 * tl2;
    double g10 = t23 - n3 * tl2;

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

    return solution;
  }
}
