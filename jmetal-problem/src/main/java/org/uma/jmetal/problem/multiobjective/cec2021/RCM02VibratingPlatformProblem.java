package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class RCM02VibratingPlatformProblem extends AbstractDoubleProblem {

  public RCM02VibratingPlatformProblem() {
    numberOfObjectives(2);
    numberOfConstraints(5);
    name("VibratingPlatformProblem");

    List<Double> lowerLimit = Arrays.asList(0.05, 0.2, 0.2, 0.35, 3.0);
    List<Double> upperLimit = Arrays.asList(0.5, 0.5, 0.6, 0.5, 6.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double d1 = solution.variables().get(0);
    double d2 = solution.variables().get(1);
    double d3 = solution.variables().get(2);
    double b = solution.variables().get(3);
    double L = solution.variables().get(4);

    double rho1 = 100;
    double rho2 = 2770;
    double rho3 = 7780;

    double E1 = 1.6;
    double E2 = 70;
    double E3 = 200;

    double c1 = 500;
    double c2 = 1500;
    double c3 = 800;

    double mu = 2 * b * (rho1 * d1 + rho2 * (d2 - d1) + rho3 * (d3 - d2));
    double EI =
        (2 * b / 3) * (E1 * Math.pow(d1, 3) + E2 * (Math.pow(d2, 3) - Math.pow(d1, 3)) + E3 * (d3
            - d2));

    double f1 = (-Math.PI) / (2 * L * L) * Math.pow(Math.abs(EI / mu), 0.5);
    double f2 = 2 * b * L * (c1 * d1 + c2 * (d2 - d1) + c3 * (d3 - d2));

    solution.objectives()[0] = f1;
    solution.objectives()[1] = f2;

    double g1 = mu * L - 2800;
    double g2 = d1 - d2;
    double g3 = d2 - d1 - 0.15;
    double g4 = d2 - d3;
    double g5 = d3 - d2 - 0.01;

    solution.constraints()[0] = g1;
    solution.constraints()[1] = g2;
    solution.constraints()[2] = g3;
    solution.constraints()[3] = g4;
    solution.constraints()[4] = g5;

    return solution ;
  }
}
