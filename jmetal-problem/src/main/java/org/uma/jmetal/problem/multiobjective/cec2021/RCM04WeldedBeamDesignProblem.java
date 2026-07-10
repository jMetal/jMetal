package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem WeldedBeamDesignProblem (RCM04)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM04WeldedBeamDesignProblem extends AbstractDoubleProblem {

  public RCM04WeldedBeamDesignProblem() {
    numberOfObjectives(2);
    numberOfConstraints(4);
    name("WeldedBeamDesignProblem");

    List<Double> lowerLimit = Arrays.asList(0.125, 0.1, 0.1, 0.125);
    List<Double> upperLimit = Arrays.asList(5.0, 10.0, 10.0, 5.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);
    double x4 = solution.variables().get(3);

    double p = 6000;
    double l = 14;
    double e = 30e6;
    double tmax = 13600;
    double sigmax = 30000;
    double g = 12e6;

    double pc = (4.013 * e * Math.sqrt((x3 * x3 + Math.pow(x4, 6)) / 36)) / (l * l)
        * (1 - x3 / (2 * l) * Math.sqrt(e / (4 * g)));
    double sigma = (6 * p * l) / (x4 * x3 * x3);
    double j = 2 * (Math.sqrt(2) * x1 * x2 * (x2 * x2 / 12 + Math.pow((x1 + x3) / 2, 2)));
    double r = Math.sqrt(x2 * x2 / 4 + Math.pow((x1 + x3) / 2, 2));
    double m = p * (l + x2 / 2);
    double tho1 = p / (Math.sqrt(2) * x1 * x2);
    double tho2 = m * r / j;
    double tho = Math.sqrt(tho1 * tho1 + 2 * tho1 * tho2 * x2 / (2 * r) + tho2 * tho2);

    solution.objectives()[0] = 1.10471 * x1 * x1 * x2 + 0.04811 * x3 * x4 * (14 + x2);
    solution.objectives()[1] = (4 * p * Math.pow(l, 3)) / (e * x4 * Math.pow(x3, 3));

    double g1 = tho - tmax;
    double g2 = sigma - sigmax;
    double g3 = x1 - x4;
    double g4 = p - pc;

    solution.constraints()[0] = -g1;
    solution.constraints()[1] = -g2;
    solution.constraints()[2] = -g3;
    solution.constraints()[3] = -g4;

    return solution;
  }
}
