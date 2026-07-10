package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem ReactorNetworkDesignProblem (RCM23)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 *
 * Equality constraints are relaxed to inequalities with tolerance {@link #EQUALITY_CONSTRAINT_EPSILON}.
 */
public class RCM23ReactorNetworkDesignProblem extends AbstractDoubleProblem {
  private static final double EQUALITY_CONSTRAINT_EPSILON = 1e-4;

  public RCM23ReactorNetworkDesignProblem() {
    numberOfObjectives(2);
    numberOfConstraints(5);
    name("ReactorNetworkDesignProblem");

    List<Double> lowerLimit = Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.00001, 0.00001);
    List<Double> upperLimit = Arrays.asList(1.0, 1.0, 1.0, 1.0, 16.0, 16.0);

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

    double k1 = 0.09755988;
    double k2 = 0.99 * k1;
    double k3 = 0.0391908;
    double k4 = 0.9 * k3;

    double f2 = Math.sqrt(x5) + Math.sqrt(x6);

    solution.objectives()[0] = -x4;
    solution.objectives()[1] = f2;

    double g1 = f2 - 4;
    double h1 = k1 * x5 * x2 + x1 - 1;
    double h2 = k3 * x5 * x3 + x3 + x1 - 1;
    double h3 = k2 * x6 * x2 - x1 + x2;
    double h4 = k4 * x6 * x4 + x2 - x1 + x4 - x3;

    solution.constraints()[0] = -g1;
    solution.constraints()[1] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h1);
    solution.constraints()[2] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h2);
    solution.constraints()[3] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h3);
    solution.constraints()[4] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h4);

    return solution;
  }
}
