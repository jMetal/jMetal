package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem TwoBarPlaneTrussProblem (RCM10)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM10TwoBarPlaneTrussProblem extends AbstractDoubleProblem {

  public RCM10TwoBarPlaneTrussProblem() {
    numberOfObjectives(2);
    numberOfConstraints(2);
    name("TwoBarPlaneTrussProblem");

    List<Double> lowerLimit = Arrays.asList(0.1, 0.5);
    List<Double> upperLimit = Arrays.asList(2.0, 2.5);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);

    double rho = 0.283;
    double h = 100;
    double p = 1e4;
    double e = 3e7;
    double rho0 = 2e4;

    solution.objectives()[0] = 2 * rho * h * x2 * Math.sqrt(1 + x1 * x1);
    solution.objectives()[1] = rho * h * Math.pow(1 + x1 * x1, 1.5) * Math.sqrt(1 + Math.pow(x1, 4))
        / (2 * Math.sqrt(2) * e * x1 * x1 * x2);

    double g1 = p * (1 + x1) * Math.sqrt(1 + x1 * x1) / (2 * Math.sqrt(2) * x1 * x2) - rho0;
    double g2 = p * (-x1 + 1) * Math.sqrt(1 + x1 * x1) / (2 * Math.sqrt(2) * x1 * x2) - rho0;

    solution.constraints()[0] = -g1;
    solution.constraints()[1] = -g2;

    return solution;
  }
}
