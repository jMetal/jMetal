package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem CantileverBeamDesignProblem (RCM16)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM16CantileverBeamDesignProblem extends AbstractDoubleProblem {

  public RCM16CantileverBeamDesignProblem() {
    numberOfObjectives(2);
    numberOfConstraints(2);
    name("CantileverBeamDesignProblem");

    List<Double> lowerLimit = Arrays.asList(0.01, 0.20);
    List<Double> upperLimit = Arrays.asList(0.05, 1.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);

    double p = 1;
    double e = 207000000;
    double sy = 300000;
    double deltaMax = 0.005;
    double rho = 7800;

    solution.objectives()[0] = 0.25 * rho * Math.PI * x2 * x1 * x1;
    solution.objectives()[1] = (64 * p * Math.pow(x2, 3)) / (3 * e * Math.PI * Math.pow(x1, 4));

    double g1 = -sy + (32 * p * x2) / (Math.PI * Math.pow(x1, 3));
    double g2 = -deltaMax + (64 * p * Math.pow(x2, 3)) / (3 * e * Math.PI * Math.pow(x1, 4));

    solution.constraints()[0] = -g1;
    solution.constraints()[1] = -g2;

    return solution;
  }
}
