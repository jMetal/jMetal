package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem FourBarPlaneTrussProblem (RCM09)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 *
 * Note: the reference Cal_par.m metadata lists 1 inequality constraint for this problem, but the
 * reference evaluator (CEC2021_func.m, func==9) computes g = zeros(ps,0) — i.e. no constraints are
 * ever assigned. This port follows the evaluator, which is the authoritative source of behavior.
 */
public class RCM09FourBarPlaneTrussProblem extends AbstractDoubleProblem {

  public RCM09FourBarPlaneTrussProblem() {
    numberOfObjectives(2);
    numberOfConstraints(0);
    name("FourBarPlaneTrussProblem");

    List<Double> lowerLimit = Arrays.asList(1.0, Math.sqrt(2), Math.sqrt(2), 1.0);
    List<Double> upperLimit = Arrays.asList(3.0, 3.0, 3.0, 3.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);
    double x4 = solution.variables().get(3);

    double f = 10;
    double e = 2e5;
    double l = 200;

    solution.objectives()[0] = l * (2 * x1 + Math.sqrt(2) * x2 + Math.sqrt(2) * x3 + x4);
    solution.objectives()[1] =
        f * l / e * (2 / x1 + 2 * Math.sqrt(2) / x2 - 2 * Math.sqrt(2) / x3 + 2 / x4);

    return solution;
  }
}
