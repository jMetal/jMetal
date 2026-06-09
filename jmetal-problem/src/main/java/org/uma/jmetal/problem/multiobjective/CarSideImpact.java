package org.uma.jmetal.problem.multiobjective;

import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Car side impact problem. Minimizes the weight of the car while minimizing the pubic force
 * experienced by a passenger and the average velocity of the V-Pillar responsible for withstanding
 * the impact load.
 *
 * <p>Source: Jain, H. and K. Deb. "An Evolutionary Many-Objective Optimization Algorithm Using
 * Reference-Point-Based Nondominated Sorting Approach, Part II: Handling Constraints and Extending
 * to an Adaptive Approach." IEEE Transactions on Evolutionary Computation, 18(4):602-622, 2014.
 *
 * <p>Also used in: Zatarain Salazar, J., Hadka, D., Reed, P., Seada, H., & Deb, K. (2024).
 * Diagnostic benchmarking of many-objective evolutionary algorithms for real-world problems.
 * Engineering Optimization, 1-22. https://doi.org/10.1080/0305215X.2024.2381818
 */
@SuppressWarnings("serial")
public class CarSideImpact extends AbstractDoubleProblem {

  public CarSideImpact() {
    numberOfObjectives(3);
    numberOfConstraints(10);
    name("CarSideImpact");

    variableBounds(
        List.of(0.5, 0.45, 0.5, 0.5, 0.875, 0.4, 0.4),
        List.of(1.5, 1.35, 1.5, 1.5, 2.625, 1.2, 1.2));
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

    double f = 4.72 - 0.5 * x4 - 0.19 * x2 * x3;
    double vmbp = 10.58 - 0.674 * x1 * x2 - 0.67275 * x2;
    double vfd = 16.45 - 0.489 * x3 * x7 - 0.843 * x5 * x6;

    solution.objectives()[0] =
        1.98 + 4.9 * x1 + 6.67 * x2 + 6.98 * x3 + 4.01 * x4 + 1.78 * x5 + 0.00001 * x6
            + 2.73 * x7;
    solution.objectives()[1] = f;
    solution.objectives()[2] = 0.5 * (vmbp + vfd);

    evaluateConstraints(solution, x1, x2, x3, x4, x5, x6, x7, f, vmbp, vfd);

    return solution;
  }

  private void evaluateConstraints(
      DoubleSolution solution,
      double x1, double x2, double x3, double x4, double x5, double x6, double x7,
      double f, double vmbp, double vfd) {

    double g1 = 1.16 - 0.3717 * x2 * x4 - 0.0092928 * x3;
    double g2 = 0.261 - 0.0159 * x1 * x2 - 0.06486 * x1 - 0.019 * x2 * x7
        + 0.0144 * x3 * x5 + 0.0154464 * x6;
    double g3 = 0.214 + 0.00817 * x5 - 0.045195 * x1 - 0.0135168 * x1
        + 0.03099 * x2 * x6 - 0.018 * x2 * x7 + 0.007176 * x3
        + 0.023232 * x3 - 0.00364 * x5 * x6 - 0.018 * x2 * x2;
    double g4 = 0.74 - 0.61 * x2 - 0.031296 * x3 - 0.031872 * x7 + 0.227 * x2 * x2;
    double g5 = 28.98 + 3.818 * x3 - 4.2 * x1 * x2 + 1.27296 * x6 - 2.68065 * x7;
    double g6 = 33.86 + 2.95 * x3 - 5.057 * x1 * x2 - 3.795 * x2 - 3.4431 * x7 + 1.45728;
    double g7 = 46.36 - 9.9 * x2 - 4.4505 * x1;

    // constraints()[i] >= 0 means feasible (jMetal convention: threshold - g_i)
    solution.constraints()[0] = 1.0 - g1;
    solution.constraints()[1] = 0.32 - g2;
    solution.constraints()[2] = 0.32 - g3;
    solution.constraints()[3] = 0.32 - g4;
    solution.constraints()[4] = 32.0 - g5;
    solution.constraints()[5] = 32.0 - g6;
    solution.constraints()[6] = 32.0 - g7;
    solution.constraints()[7] = 4.0 - f;
    solution.constraints()[8] = 9.9 - vmbp;
    solution.constraints()[9] = 15.7 - vfd;
  }
}
