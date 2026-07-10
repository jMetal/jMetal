package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem ProcessSynthesisAndDesignProblem (RCM26)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 *
 * Equality constraints are relaxed to inequalities with tolerance {@link #EQUALITY_CONSTRAINT_EPSILON}.
 */
public class RCM26ProcessSynthesisAndDesignProblem extends AbstractDoubleProblem {
  private static final double EQUALITY_CONSTRAINT_EPSILON = 1e-4;

  public RCM26ProcessSynthesisAndDesignProblem() {
    numberOfObjectives(2);
    numberOfConstraints(2);
    name("ProcessSynthesisAndDesignProblem");

    List<Double> lowerLimit = Arrays.asList(0.5, 0.5, -0.49);
    List<Double> upperLimit = Arrays.asList(1.4, 1.4, 1.49);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = Math.round(solution.variables().get(2));

    solution.objectives()[0] = -x3 + x2 + 2 * x1;
    solution.objectives()[1] = -x1 * x1 - x2 + x1 * x3;

    double h1 = -2 * Math.exp(-x2) + x1;
    double g1 = x2 - x1 + x3;

    solution.constraints()[0] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h1);
    solution.constraints()[1] = -g1;

    return solution;
  }
}
