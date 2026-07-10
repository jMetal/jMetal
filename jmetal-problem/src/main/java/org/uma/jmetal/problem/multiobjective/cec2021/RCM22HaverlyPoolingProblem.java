package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem HaverlyPoolingProblem (RCM22)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 *
 * Equality constraints are relaxed to inequalities with tolerance {@link #EQUALITY_CONSTRAINT_EPSILON},
 * following the constraint-violation definition of the reference technical report:
 * a solution is feasible on an equality constraint h(x) = 0 when |h(x)| &lt;= epsilon.
 */
public class RCM22HaverlyPoolingProblem extends AbstractDoubleProblem {
  private static final double EQUALITY_CONSTRAINT_EPSILON = 1e-4;

  public RCM22HaverlyPoolingProblem() {
    numberOfObjectives(2);
    numberOfConstraints(6);
    name("HaverlyPoolingProblem");

    List<Double> lowerLimit = Arrays.asList(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    List<Double> upperLimit =
        Arrays.asList(100.0, 200.0, 100.0, 100.0, 100.0, 100.0, 200.0, 100.0, 200.0);

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

    solution.objectives()[0] = -9 * x1 - 15 * x2 + 6 * x3 + 16 * x4;
    solution.objectives()[1] = 10 * (x5 + x6);

    double g1 = x9 * x7 + 2 * x5 - 2.5 * x1;
    double g2 = x9 * x8 + 2 * x6 - 1.5 * x2;
    double h1 = x7 + x8 - x4 - x3;
    double h2 = x1 - x5 - x7;
    double h3 = x2 - x6 - x8;
    double h4 = x9 * x7 + x9 * x8 - 3 * x3 - x4;

    solution.constraints()[0] = -g1;
    solution.constraints()[1] = -g2;
    solution.constraints()[2] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h1);
    solution.constraints()[3] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h2);
    solution.constraints()[4] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h3);
    solution.constraints()[5] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(h4);

    return solution;
  }
}
