package org.uma.jmetal.problem.multiobjective.re;

import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem RE35. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
public class RE35 extends AbstractDoubleProblem {

  private int numberOfOriginalConstraints = 11;

  /** Constructor */
  public RE35() {
    setNumberOfVariables(7);
    setNumberOfObjectives(3);
    setNumberOfConstraints(0);
    setName("RE35");

    List<Double> lowerLimit = List.of(2.6, 0.7, 17.0, 7.3, 7.3, 2.9, 5.0);
    List<Double> upperLimit = List.of(3.6, 0.8, 28.0, 8.3, 8.3, 3.9, 5.5);
    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = Math.rint(solution.variables().get(2));
    double x4 = solution.variables().get(3);
    double x5 = solution.variables().get(4);
    double x6 = solution.variables().get(5);
    double x7 = solution.variables().get(6);

    solution.objectives()[0] =
        0.7854 * x1 * (x2 * x2) * (((10.0 * x3 * x3) / 3.0) + (14.933 * x3) - 43.0934)
            - 1.508 * x1 * (x6 * x6 + x7 * x7)
            + 7.477 * (x6 * x6 * x6 + x7 * x7 * x7)
            + 0.7854 * (x4 * x6 * x6 + x5 * x7 * x7);

    double tmpVar = Math.pow((745.0 * x4) / (x2 * x3), 2.0) + 1.69 * 1e7;
    solution.objectives()[1] = Math.sqrt(tmpVar) / (0.1 * x6 * x6 * x6);

    double[] g = new double[numberOfOriginalConstraints];
    g[0] = -(1.0 / (x1 * x2 * x2 * x3)) + 1.0 / 27.0;
    g[1] = -(1.0 / (x1 * x2 * x2 * x3 * x3)) + 1.0 / 397.5;
    g[2] = -(x4 * x4 * x4) / (x2 * x3 * x6 * x6 * x6 * x6) + 1.0 / 1.93;
    g[3] = -(x5 * x5 * x5) / (x2 * x3 * x7 * x7 * x7 * x7) + 1.0 / 1.93;
    g[4] = -(x2 * x3) + 40.0;
    g[5] = -(x1 / x2) + 12.0;
    g[6] = -5.0 + (x1 / x2);
    g[7] = -1.9 + x4 - 1.5 * x6;
    g[8] = -1.9 + x5 - 1.1 * x7;
    g[9] = -solution.objectives()[1] + 1300.0;
    tmpVar = Math.pow((745.0 * x5) / (x2 * x3), 2.0) + 1.575 * 1e8;
    g[10] = -Math.sqrt(tmpVar) / (0.1 * x7 * x7 * x7) + 1100.0;

    for (int i = 0; i < numberOfOriginalConstraints; i++) {
      if (g[i] < 0.0) g[i] = -g[i];
      else g[i] = 0;
    }

    solution.objectives()[2] = g[0] + g[1] + g[2] + g[3] + g[4] + g[5] + g[6] + g[7] + g[8] + g[9] + g[10];

    return solution;
  }
}
