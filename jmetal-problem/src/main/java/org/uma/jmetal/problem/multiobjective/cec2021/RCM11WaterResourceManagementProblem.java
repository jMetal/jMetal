package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem WaterResourceManagementProblem (RCM11)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 *
 * Note: the reference MATLAB code negates all 7 constraints ({@code g = -g;}) after computing them
 * in "value <= threshold" form; that negation cancels out with the sign flip needed for jMetal's
 * convention (feasible iff constraint value &gt;= 0), so the constraints below are assigned directly
 * from the pre-negation MATLAB expressions.
 */
public class RCM11WaterResourceManagementProblem extends AbstractDoubleProblem {

  public RCM11WaterResourceManagementProblem() {
    numberOfObjectives(5);
    numberOfConstraints(7);
    name("WaterResourceManagementProblem");

    List<Double> lowerLimit = Arrays.asList(0.01, 0.01, 0.01);
    List<Double> upperLimit = Arrays.asList(0.45, 0.1, 0.1);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);

    solution.objectives()[0] = 106780.37 * (x2 + x3) + 61704.67;
    solution.objectives()[1] = 3000 * x1;
    solution.objectives()[2] = 305700 * 2289 * x2 / Math.pow(0.06 * 2289, 0.65);
    solution.objectives()[3] = 250 * 2289 * Math.exp(-39.75 * x2 + 9.9 * x3 + 2.74);
    solution.objectives()[4] = 25 * (1.39 / (x1 * x2) + 4940 * x3 - 80);

    solution.constraints()[0] = 1 - (0.00139 / (x1 * x2) + 4.94 * x3 - 0.08);
    solution.constraints()[1] = 1 - (0.000306 / (x1 * x2) + 1.082 * x3 - 0.0986);
    solution.constraints()[2] = 50000 - (12.307 / (x1 * x2) + 49408.24 * x3 + 4051.02);
    solution.constraints()[3] = 16000 - (2.098 / (x1 * x2) + 8046.33 * x3 - 696.71);
    solution.constraints()[4] = 10000 - (2.138 / (x1 * x2) + 7883.39 * x3 - 705.04);
    solution.constraints()[5] = 2000 - (0.417 * x1 * x2 + 1721.26 * x3 - 136.54);
    solution.constraints()[6] = 550 - (0.164 / (x1 * x2) + 631.13 * x3 - 54.48);

    return solution;
  }
}
