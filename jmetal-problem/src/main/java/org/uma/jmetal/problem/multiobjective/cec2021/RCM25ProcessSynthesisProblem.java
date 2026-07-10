package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem ProcessSynthesisProblem (RCM25)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM25ProcessSynthesisProblem extends AbstractDoubleProblem {

  public RCM25ProcessSynthesisProblem() {
    numberOfObjectives(2);
    numberOfConstraints(2);
    name("ProcessSynthesisProblem");

    List<Double> lowerLimit = Arrays.asList(0.0, -0.49);
    List<Double> upperLimit = Arrays.asList(1.6, 1.49);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = Math.round(solution.variables().get(1));

    double f2 = -x1 * x1 - x2;

    solution.objectives()[0] = x2 + 2 * x1;
    solution.objectives()[1] = f2;

    double g1 = f2 + 1.25;
    double g2 = x1 + x2 - 1.6;

    solution.constraints()[0] = -g1;
    solution.constraints()[1] = -g2;

    return solution;
  }
}
