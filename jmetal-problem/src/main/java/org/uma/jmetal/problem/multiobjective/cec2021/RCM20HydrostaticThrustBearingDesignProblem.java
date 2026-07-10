package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem HydrostaticThrustBearingDesignProblem (RCM20)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
public class RCM20HydrostaticThrustBearingDesignProblem extends AbstractDoubleProblem {

  public RCM20HydrostaticThrustBearingDesignProblem() {
    numberOfObjectives(2);
    numberOfConstraints(7);
    name("HydrostaticThrustBearingDesignProblem");

    List<Double> lowerLimit = Arrays.asList(1.0, 1.0, 1e-6, 1.0);
    List<Double> upperLimit = Arrays.asList(16.0, 16.0, 16e-6, 16.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double r = solution.variables().get(0);
    double ro = solution.variables().get(1);
    double mu = solution.variables().get(2);
    double q = solution.variables().get(3);

    double gamma = 0.0307;
    double c = 0.5;
    double n = -3.55;
    double c1 = 10.04;
    double ws = 101000;
    double pmax = 1000;
    double delTmax = 50;
    double hmin = 0.001;
    double gg = 386.4;
    double nSpeed = 750;

    double p = (Math.log10(Math.log10(8.122e6 * mu + 0.8)) - c1) / n;
    double delT = 2 * (Math.pow(10, p) - 560);
    double ef = 9336 * q * gamma * c * delT;
    double h = Math.pow(2 * Math.PI * nSpeed / 60, 2) * 2 * Math.PI * mu / ef
        * (Math.pow(r, 4) / 4 - Math.pow(ro, 4) / 4) - 1e-5;
    double po = (6 * mu * q / (Math.PI * Math.pow(h, 3))) * Math.log(r / ro);
    double w = Math.PI * po / 2 * (r * r - ro * ro) / (Math.log(r / ro) - 1e-5);

    double f2 = gamma / (gg * po) * (q / (2 * Math.PI * r * h));

    solution.objectives()[0] = (q * po / 0.7 + ef) / 12;
    solution.objectives()[1] = f2;

    double g1 = ws - w;
    double g2 = po - pmax;
    double g3 = delT - delTmax;
    double g4 = hmin - h;
    double g5 = ro - r;
    double g6 = f2 - 0.001;
    double g7 = w / (Math.PI * (r * r - ro * ro) + 1e-5) - 5000;

    solution.constraints()[0] = -g1;
    solution.constraints()[1] = -g2;
    solution.constraints()[2] = -g3;
    solution.constraints()[3] = -g4;
    solution.constraints()[4] = -g5;
    solution.constraints()[5] = -g6;
    solution.constraints()[6] = -g7;

    return solution;
  }
}
