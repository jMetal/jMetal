package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem GearTrainDesignProblem (RCM07)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM07GearTrainDesignProblem extends AbstractDoubleProblem {

  public RCM07GearTrainDesignProblem() {
    numberOfObjectives(2);
    numberOfConstraints(1);
    name("GearTrainDesignProblem");

    List<Double> lowerLimit = Arrays.asList(11.51, 11.51, 11.51, 11.51);
    List<Double> upperLimit = Arrays.asList(60.49, 60.49, 60.49, 60.49);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);
    double x4 = solution.variables().get(3);

    double f1 = Math.abs(6.931 - x3 * x4 / (x1 * x2));
    double f2 = Math.max(Math.max(x1, x2), Math.max(x3, x4));

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;

    double g1 = f1 / 6.931 - 0.5;

    solution.constraints()[0] = -g1;

    return solution;
  }
}
