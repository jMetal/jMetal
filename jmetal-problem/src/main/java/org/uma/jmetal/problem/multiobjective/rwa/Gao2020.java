package org.uma.jmetal.problem.multiobjective.rwa;

import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class Gao2020 extends AbstractDoubleProblem {

  public Gao2020() {
    numberOfObjectives(3);
    name("Gao2020");

    List<Double> lowerLimit = List.of(40.0, 0.35, 333.0, 20.0, 3000.0, 0.1, 308.0, 150.0, 0.1);
    List<Double> upperLimit = List.of(100.0, 0.5, 363.0, 40.0, 4000.0, 3.0, 328.0, 200.0, 2.0);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double A = solution.variables().get(0);
    double B = solution.variables().get(1);
    double C = solution.variables().get(2);
    double D = solution.variables().get(3);
    double E = solution.variables().get(4);
    double F = solution.variables().get(5);
    double G = solution.variables().get(6);
    double H = solution.variables().get(7);
    double J = solution.variables().get(8);

    double t_eff = 171.33 + 23.25 * A - 8.61 * B - 59.85 * C - 66.12 * D - 15.29 * E
        - 83.32 * F + 37.72 * G + 12.67 * H + 0.46 * J - 0.47 * A * B
        - 0.30 * A * C - 6.22 * A * D - 0.62 * A * E - 42.48 * A * F
        + 3.11 * A * G + 4.45 * A * H - 0.22 * A * J + 7.46 * B * C
        + 3.28 * B * D + 1.28 * B * E + 1.02 * B * F - 4.02 * B * G
        - 2.29 * B * H - 0.16 * B * J + 19.25 * C * D - 14.83 * C * E
        + 5.07 * C * F - 37.61 * C * G - 9.11 * C * H - 0.32 * C * J
        + 8.53 * D * E + 18.46 * D * F - 14.28 * D * G - 7.05 * D * H
        - 0.24 * D * J + 2.05 * E * F + 15.73 * E * G - 0.77 * E * H
        - 0.29 * E * J - 4.77 * F * G + 2.07 * F * H + 0.64 * F * J
        + 3.41 * G * H + 1.76 * G * J + 0.48 * H * J + 3.64 * A * A
        - 0.99 * B * B + 30.5 * C * C + 21.63 * D * D + 1.72 * E * E
        + 72.42 * F * F + 11.2 * G * G + 1.86 * H * H - 0.79 * J * J;

    double Q_eff = 577.73 - 1.22 * A - 19.56 * B + 102.05 * C - 1.83 * D + 27.28 * E
        + 2.52 * F + 5.43 * G + 37.48 * H + 0.45 * J + 2.94 * A * B
        - 2.96 * A * C + 0.66 * A * D + 0.09 * A * E - 0.43 * A * F
        + 0.12 * A * G - 0.43 * A * H - 0.7 * A * J + 8.05 * B * C
        + 0.53 * B * D + 4.43 * B * E - 0.6 * B * F - 0.46 * B * G
        - 4.97 * B * H + 0.046 * B * J + 0.42 * C * D + 6.03 * C * E
        + 0.21 * C * F + 2.63 * C * G + 0.17 * C * H - 0.43 * C * J
        + 6.34 * D * E + 6.36 * D * F + 0.19 * D * G - 0.22 * D * H
        + 0.39 * D * J - 7.09 * E * F + 3.06 * E * G - 0.15 * E * H
        + 0.68 * E * J - 0.2 * F * G + 0.14 * F * H + 0.88 * F * J
        + 0.45 * G * H - 0.014 * G * J + 0.99 * H * J + 0.55 * A * A
        - 4.97 * B * B - 0.47 * C * C - 0.91 * D * D - 2.08 * E * E
        - 1.43 * F * F + 0.43 * G * G + 1.06 * H * H + 0.98 * J * J;

    double Phi_ex = 0.81 - 9.26e-3 * A + 0.014 * B - 0.029 * C - 7.69e-4 * D
        + 4.05e-3 * E + 0.029 * F + 0.075 * G - 0.012 * H
        - 1.04e-3 * J - 2.63e-3 * A * B + 1.34e-4 * A * C
        - 1.48e-3 * A * D - 7.04e-4 * A * E + 0.013 * A * F
        + 6.55e-4 * A * G - 9.71e-3 * A * H + 1.08e-3 * A * J
        + 2.54e-3 * B * C - 4.83e-4 * B * D + 9.63e-4 * B * E
        + 1.21e-3 * B * F - 7.02e-3 * B * G - 1.21e-3 * B * H
        + 1.94e-5 * B * J - 1.15e-3 * C * D + 3.60e-3 * C * E
        + 5.60e-3 * C * F - 0.026 * C * G - 4.01e-3 * C * H
        + 1.35e-3 * C * J - 6.93e-3 * D * E - 3.16e-3 * D * F
        - 2.38e-4 * D * G + 7.32e-4 * D * H + 4.69e-4 * D * J
        + 8.18e-3 * E * F - 5.74e-3 * E * G + 1.44e-4 * E * H
        - 9.95e-5 * E * J - 2.09e-3 * F * G - 65e-4 * F * H
        - 1.99e-3 * F * J + 4.95e-3 * G * H + 8.70e-4 * G * J
        + 4.55e-4 * H * J - 9.32e-4 * A * A - 7.61e-4 * B * B
        + 0.016 * C * C + 1.24e-3 * D * D + 9.61e-4 * E * E
        - 0.024 * F * F - 8.63e-3 * G * G - 1.90e-4 * H * H
        - 7.56e-4 * J * J;

    solution.objectives()[0] = t_eff; /* Minimize */
    solution.objectives()[1] = -Q_eff; /* Maximize */
    solution.objectives()[2] = -Phi_ex; /* Maximize */

    return solution ;
  }
}

