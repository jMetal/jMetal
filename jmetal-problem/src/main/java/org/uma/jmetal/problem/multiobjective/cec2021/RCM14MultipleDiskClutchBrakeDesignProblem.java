package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem MultipleDiskClutchBrakeDesignProblem (RCM14)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM14MultipleDiskClutchBrakeDesignProblem extends AbstractDoubleProblem {

  public RCM14MultipleDiskClutchBrakeDesignProblem() {
    numberOfObjectives(2);
    numberOfConstraints(8);
    name("MultipleDiskClutchBrakeDesignProblem");

    List<Double> lowerLimit = Arrays.asList(60.0, 90.0, 1.0, 0.0, 2.0);
    List<Double> upperLimit = Arrays.asList(80.0, 110.0, 3.0, 1000.0, 9.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);
    double x4 = solution.variables().get(3);
    double x5 = solution.variables().get(4);

    double mf = 3;
    double ms = 40;
    double iz = 55;
    double n = 250;
    double tmax = 15;
    double s = 1.5;
    double delta = 0.5;
    double vsrmax = 10;
    double rho = 0.0000078;
    double pmax = 1;
    double mu = 0.6;
    double lmax = 30;
    double delR = 20;

    double rsr = 2.0 / 3 * (Math.pow(x2, 3) - Math.pow(x1, 3)) / (x2 * x2 * x1 * x1);
    double vsr = Math.PI * rsr * n / 30;
    double a = Math.PI * (x2 * x2 - x1 * x1);
    double prz = x4 / a;
    double w = Math.PI * n / 30;
    double mh = 2.0 / 3 * mu * x4 * x5 * (Math.pow(x2, 3) - Math.pow(x1, 3)) / (x2 * x2 - x1 * x1);
    double t = iz * w / (mh + mf);

    solution.objectives()[0] = Math.PI * (x2 * x2 - x1 * x1) * x3 * (x5 + 1) * rho;
    solution.objectives()[1] = t;

    double g1 = -x2 + x1 + delR;
    double g2 = (x5 + 1) * (x3 + delta) - lmax;
    double g3 = prz - pmax;
    double g4 = prz * vsr - pmax * vsrmax;
    double g5 = vsr - vsrmax;
    double g6 = t - tmax;
    double g7 = s * ms - mh;
    double g8 = -t;

    solution.constraints()[0] = -g1;
    solution.constraints()[1] = -g2;
    solution.constraints()[2] = -g3;
    solution.constraints()[3] = -g4;
    solution.constraints()[4] = -g5;
    solution.constraints()[5] = -g6;
    solution.constraints()[6] = -g7;
    solution.constraints()[7] = -g8;

    return solution;
  }
}
