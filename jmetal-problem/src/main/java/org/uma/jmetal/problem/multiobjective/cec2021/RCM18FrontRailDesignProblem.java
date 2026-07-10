package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem FrontRailDesignProblem (RCM18)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 *
 * Note: the reference MATLAB code negates all 3 constraints ({@code g = -g;}) after computing them
 * in "value <= 0" form; that negation cancels out with the sign flip needed for jMetal's convention
 * (feasible iff constraint value &gt;= 0), so the constraints below are assigned directly from the
 * pre-negation MATLAB expressions.
 */
public class RCM18FrontRailDesignProblem extends AbstractDoubleProblem {

  public RCM18FrontRailDesignProblem() {
    numberOfObjectives(2);
    numberOfConstraints(3);
    name("FrontRailDesignProblem");

    List<Double> lowerLimit = Arrays.asList(136.0, 56.0, 1.4);
    List<Double> upperLimit = Arrays.asList(146.0, 68.0, 2.2);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double hh = solution.variables().get(0);
    double w = solution.variables().get(1);
    double t = solution.variables().get(2);

    double ea = 14496.5;
    double fa = 234.9;
    double e = -70973.4 + 958.656 * w + 614.173 * hh - 3.827 * w * hh + 57.023 * w * t
        + 63.274 * hh * t - 3.582 * w * w - 1.4842 * hh * hh - 1890.174 * t * t;
    double f = 111.854 - 20.210 * w + 7.560 * hh - 0.025 * w * hh + 2.731 * w * t
        - 1.479 * hh * t + 0.165 * w * w;

    solution.objectives()[0] = ea / e;
    solution.objectives()[1] = f / fa;

    solution.constraints()[0] = (hh - 136) * (146 - hh);
    solution.constraints()[1] = (w - 58) * (66 - w);
    solution.constraints()[2] = (t - 1.4) * (2.2 - t);

    return solution;
  }
}
