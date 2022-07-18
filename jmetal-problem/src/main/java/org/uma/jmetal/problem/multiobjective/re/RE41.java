package org.uma.jmetal.problem.multiobjective.re;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem RE41. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
public class RE41 extends AbstractDoubleProblem {

  private int numberOfOriginalConstraints = 10;

  /** Constructor */
  public RE41() {
    setNumberOfObjectives(4);
    setNumberOfConstraints(0);
    setName("RE41");

    List<Double> lowerLimit = List.of(0.5, 0.45, 0.5, 0.5, 0.875, 0.4, 0.4);
    List<Double> upperLimit = List.of(1.5, 1.35, 1.5, 1.5, 2.625, 1.2, 1.2);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = Math.rint(solution.variables().get(0));
    double x2 = Math.rint(solution.variables().get(1));
    double x3 = Math.rint(solution.variables().get(2));
    double x4 = Math.rint(solution.variables().get(3));
    double x5 = Math.rint(solution.variables().get(4));
    double x6 = Math.rint(solution.variables().get(5));
    double x7 = Math.rint(solution.variables().get(6));

    solution.objectives()[0] =
        1.98 + 4.9 * x1 + 6.67 * x2 + 6.98 * x3 + 4.01 * x4 + 1.78 * x5 + 0.00001 * x6 + 2.73 * x7;
    solution.objectives()[1] = 4.72 - 0.5 * x4 - 0.19 * x2 * x3;

    double Vmbp = 10.58 - 0.674 * x1 * x2 - 0.67275 * x2;
    double Vfd = 16.45 - 0.489 * x3 * x7 - 0.843 * x5 * x6;
    solution.objectives()[2] = 0.5 * (Vmbp + Vfd);

    double[] g = new double[numberOfOriginalConstraints];

    g[0] = 1 - (1.16 - 0.3717 * x2 * x4 - 0.0092928 * x3);
    g[1] =
        0.32
            - (0.261
                - 0.0159 * x1 * x2
                - 0.06486 * x1
                - 0.019 * x2 * x7
                + 0.0144 * x3 * x5
                + 0.0154464 * x6);
    g[2] =
        0.32
            - (0.214
                + 0.00817 * x5
                - 0.045195 * x1
                - 0.0135168 * x1
                + 0.03099 * x2 * x6
                - 0.018 * x2 * x7
                + 0.007176 * x3
                + 0.023232 * x3
                - 0.00364 * x5 * x6
                - 0.018 * x2 * x2);
    g[3] = 0.32 - (0.74 - 0.61 * x2 - 0.031296 * x3 - 0.031872 * x7 + 0.227 * x2 * x2);
    g[4] = 32 - (28.98 + 3.818 * x3 - 4.2 * x1 * x2 + 1.27296 * x6 - 2.68065 * x7);
    g[5] = 32 - (33.86 + 2.95 * x3 - 5.057 * x1 * x2 - 3.795 * x2 - 3.4431 * x7 + 1.45728);
    g[6] = 32 - (46.36 - 9.9 * x2 - 4.4505 * x1);
    g[7] = 4 - solution.objectives()[1];
    g[8] = 9.9 - Vmbp;
    g[9] = 15.7 - Vfd;

    for (int i = 0; i < numberOfOriginalConstraints; i++) {
      if (g[i] < 0.0) g[i] = -g[i];
      else g[i] = 0;
    }

      double valueObjectiveThree = 0.0;
      int bound = numberOfOriginalConstraints;
      for (int i = 0; i < bound; i++) {
          double v = g[i];
          valueObjectiveThree += v;
      }

      solution.objectives()[3] = valueObjectiveThree;

    return solution;
  }
}
