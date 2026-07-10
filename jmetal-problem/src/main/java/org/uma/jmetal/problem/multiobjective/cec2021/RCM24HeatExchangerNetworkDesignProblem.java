package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem HeatExchangerNetworkDesignProblem (RCM24)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 *
 * Equality constraints are relaxed to inequalities with tolerance {@link #EQUALITY_CONSTRAINT_EPSILON}.
 */
public class RCM24HeatExchangerNetworkDesignProblem extends AbstractDoubleProblem {
  private static final double EQUALITY_CONSTRAINT_EPSILON = 1e-4;

  public RCM24HeatExchangerNetworkDesignProblem() {
    numberOfObjectives(3);
    numberOfConstraints(6);
    name("HeatExchangerNetworkDesignProblem");

    List<Double> lowerLimit = Arrays.asList(0.0, 0.0, 0.0, 0.0, 1000.0, 0.0, 100.0, 100.0, 100.0);
    List<Double> upperLimit =
        Arrays.asList(10.0, 200.0, 100.0, 200.0, 2000000.0, 600.0, 600.0, 600.0, 900.0);

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
    double x8 = solution.variables().get(7);
    double x9 = solution.variables().get(8);

    solution.objectives()[0] = 35 * Math.pow(x1, 0.6) + 35 * Math.pow(x2, 0.6);
    solution.objectives()[1] = 200 * x1 * x4 - x3;
    solution.objectives()[2] = 200 * x2 * x6 - x5;

    double h1 = x3 - 1e4 * (x7 - 100);
    double h2 = x5 - 1e4 * (300 - x7);
    double h3 = x3 - 1e4 * (600 - x8);
    double h4 = x5 - 1e4 * (900 - x9);
    double h5 = x4 * Math.log(Math.abs(x8 - 100) + 1e-6)
        - x4 * Math.log(Math.abs(600 - x7) + 1e-6) - x8 + x7 + 500;
    double h6 = x6 * Math.log(Math.abs(x9 - x7) + 1e-6) - x6 * Math.log(600) - x9 + x7 + 600;

    solution.constraints()[0] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h1);
    solution.constraints()[1] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h2);
    solution.constraints()[2] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h3);
    solution.constraints()[3] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h4);
    solution.constraints()[4] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h5);
    solution.constraints()[5] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h6);

    return solution;
  }
}
