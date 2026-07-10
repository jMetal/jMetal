package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem TwoReactorProblem (RCM28)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 *
 * Equality constraints are relaxed to inequalities with tolerance {@link #EQUALITY_CONSTRAINT_EPSILON}.
 */
public class RCM28TwoReactorProblem extends AbstractDoubleProblem {
  private static final double EQUALITY_CONSTRAINT_EPSILON = 1e-4;

  public RCM28TwoReactorProblem() {
    numberOfObjectives(2);
    numberOfConstraints(8);
    name("TwoReactorProblem");

    List<Double> lowerLimit = Arrays.asList(0.0, 0.0, 0.0, 0.0, -0.49, -0.49, 0.0);
    List<Double> upperLimit = Arrays.asList(20.0, 20.0, 10.0, 10.0, 1.49, 1.49, 40.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double v1 = solution.variables().get(2);
    double v2 = solution.variables().get(3);
    double y1 = Math.round(solution.variables().get(4));
    double y2 = Math.round(solution.variables().get(5));
    double x = solution.variables().get(6);

    double z1 = 0.9 * (1 - Math.exp(-0.5 * v1)) * x1;
    double z2 = 0.8 * (1 - Math.exp(-0.4 * v2)) * x2;

    solution.objectives()[0] = 7.5 * y1 + 5.5 * y2 + 7 * v1 + 6 * v2 + 5 * x;
    solution.objectives()[1] = x1 + x2;

    double h1 = y1 + y2 - 1;
    double h2 = z1 + z2 - 10;
    double h3 = x1 + x2 - x;
    double h4 = z1 * y1 + z2 * y2 - 10;
    double g1 = v1 - 10 * y1 - 1e-6;
    double g2 = v2 - 10 * y2;
    double g3 = x1 - 20 * y1;
    double g4 = x2 - 20 * y2;

    solution.constraints()[0] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h1);
    solution.constraints()[1] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h2);
    solution.constraints()[2] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h3);
    solution.constraints()[3] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h4);
    solution.constraints()[4] = -g1;
    solution.constraints()[5] = -g2;
    solution.constraints()[6] = -g3;
    solution.constraints()[7] = -g4;

    return solution;
  }
}
