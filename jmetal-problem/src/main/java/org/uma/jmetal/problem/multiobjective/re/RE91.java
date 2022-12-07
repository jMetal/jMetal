package org.uma.jmetal.problem.multiobjective.re;

import java.util.List;
import java.util.Random;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem RE61. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
public class RE91 extends AbstractDoubleProblem {
  private Random random = new Random() ;

  /** Constructor */
  public RE91() {
    numberOfObjectives(9);
    numberOfConstraints(0);
    name("RE91");

    List<Double> lowerLimit = List.of(0.5, 0.45, 0.5, 0.5, 0.875, 0.4, 0.4);
    List<Double> upperLimit = List.of(1.5, 1.35, 1.5, 1.5, 2.265, 1.2, 1.2);

    variableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] x = new double[11];
    for (int i = 0; i < numberOfVariables(); i++) {
      x[i] = solution.variables().get(i);
    }

    x[7] = 0.006 * (random.nextGaussian()) + 0.345;
    x[8] = 0.006 * (random.nextGaussian()) + 0.192;
    x[9] = 10 * (random.nextGaussian()) + 0.0;
    x[10] = 10 * (random.nextGaussian()) + 0.0;

    solution.objectives()[0] =
        1.98
            + 4.9 * x[0]
            + 6.67 * x[1]
            + 6.98 * x[2]
            + 4.01 * x[3]
            + 1.75 * x[4]
            + 0.00001 * x[5]
            + 2.73 * x[6];
    solution.objectives()[1] =
        Math.max(
            0.0,
            (1.16
                    - 0.3717 * x[1] * x[3]
                    - 0.00931 * x[1] * x[9]
                    - 0.484 * x[2] * x[8]
                    + 0.01343 * x[5] * x[9])
                / 1.0);
    solution.objectives()[2] =
        Math.max(
            0.0,
            (0.261
                    - 0.0159 * x[0] * x[1]
                    - 0.188 * x[0] * x[7]
                    - 0.019 * x[1] * x[6]
                    + 0.0144 * x[2] * x[4]
                    + 0.87570001 * x[4] * x[9]
                    + 0.08045 * x[5] * x[8]
                    + 0.00139 * x[7] * x[10]
                    + 0.00001575 * x[9] * x[10])
                / 0.32);
    solution.objectives()[3] =
        Math.max(
            0.0,
            (0.214
                    + 0.00817 * x[4]
                    - 0.131 * x[0] * x[7]
                    - 0.0704 * x[0] * x[8]
                    + 0.03099 * x[1] * x[5]
                    - 0.018 * x[1] * x[6]
                    + 0.0208 * x[2] * x[7]
                    + 0.121 * x[2] * x[8]
                    - 0.00364 * x[4] * x[5]
                    + 0.0007715 * x[4] * x[9]
                    - 0.0005354 * x[5] * x[9]
                    + 0.00121 * x[7] * x[10]
                    + 0.00184 * x[8] * x[9]
                    - 0.018 * x[1] * x[1])
                / 0.32);
    solution.objectives()[4] =
        Math.max(
            0.0,
            (0.74
                    - 0.61 * x[1]
                    - 0.163 * x[2] * x[7]
                    + 0.001232 * x[2] * x[9]
                    - 0.166 * x[6] * x[8]
                    + 0.227 * x[1] * x[1])
                / 0.32);

    double temp =
        ((28.98
                    + 3.818 * x[2]
                    - 4.2 * x[0] * x[1]
                    + 0.0207 * x[4] * x[9]
                    + 6.63 * x[5] * x[8]
                    - 7.77 * x[6] * x[7]
                    + 0.32 * x[8] * x[9])
                + (33.86
                    + 2.95 * x[2]
                    + 0.1792 * x[9]
                    - 5.057 * x[0] * x[1]
                    - 11 * x[1] * x[7]
                    - 0.0215 * x[4] * x[9]
                    - 9.98 * x[6] * x[7]
                    + 22 * x[7] * x[8])
                + (46.36 - 9.9 * x[1] - 12.9 * x[0] * x[7] + 0.1107 * x[2] * x[9]))
            / 3;

    solution.objectives()[5] = Math.max(0.0, temp / 32);
    solution.objectives()[6] =
        Math.max(
            0.0,
            (4.72
                    - 0.5 * x[3]
                    - 0.19 * x[1] * x[2]
                    - 0.0122 * x[3] * x[9]
                    + 0.009325 * x[5] * x[9]
                    + 0.000191 * x[10] * x[10])
                / 4.0);
    solution.objectives()[7] =
    Math.max(
            0.0,
            (10.58
                    - 0.674 * x[0] * x[1]
                    - 1.95 * x[1] * x[7]
                    + 0.02054 * x[2] * x[9]
                    - 0.0198 * x[3] * x[9]
                    + 0.028 * x[5] * x[9])
                / 9.9);
    solution.objectives()[8] =
    Math.max(
            0.0,
            (16.45
                    - 0.489 * x[2] * x[6]
                    - 0.843 * x[4] * x[5]
                    + 0.0432 * x[8] * x[9]
                    - 0.0556 * x[8] * x[10]
                    - 0.000786 * x[10] * x[10])
                / 15.7);

    return solution;
  }
}
