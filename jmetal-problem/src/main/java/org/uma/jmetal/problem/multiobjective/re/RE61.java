package org.uma.jmetal.problem.multiobjective.re;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem RE61. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
public class RE61 extends AbstractDoubleProblem {

  private int numberOfOriginalConstraints = 7;

  /** Constructor */
  public RE61() {
    setNumberOfObjectives(6);
    setNumberOfConstraints(0);
    setName("RE61");

    List<Double> lowerLimit = List.of(0.01, 0.01, 0.01);
    List<Double> upperLimit = List.of(0.45, 0.10, 0.10);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
      double[] x = new double[10];
      int count = 0;
      int bound = getNumberOfVariables();
      for (int i1 = 0; i1 < bound; i1++) {
          double v1 = solution.variables().get(i1);
          if (x.length == count) x = Arrays.copyOf(x, count * 2);
          x[count++] = v1;
      }
      x = Arrays.copyOfRange(x, 0, count);

      solution.objectives()[0] = 106780.37 * (x[1] + x[2]) + 61704.67;
    solution.objectives()[1] = 3000 * x[0];
    solution.objectives()[2] = 305700 * 2289 * x[1] / Math.pow(0.06 * 2289, 0.65);
    solution.objectives()[3] = 250 * 2289 * Math.exp(-39.75 * x[1] + 9.9 * x[2] + 2.74);
    solution.objectives()[4] = 25 * (1.39 / (x[0] * x[1]) + 4940 * x[2] - 80);

    double[] g = new double[numberOfOriginalConstraints];
    g[0] = 1 - (0.00139 / (x[0] * x[1]) + 4.94 * x[2] - 0.08);
    g[1] = 1 - (0.000306 / (x[0] * x[1]) + 1.082 * x[2] - 0.0986);
    g[2] = 50000 - (12.307 / (x[0] * x[1]) + 49408.24 * x[2] + 4051.02);
    g[3] = 16000 - (2.098 / (x[0] * x[1]) + 8046.33 * x[2] - 696.71);
    g[4] = 10000 - (2.138 / (x[0] * x[1]) + 7883.39 * x[2] - 705.04);
    g[5] = 2000 - (0.417 * x[0] * x[1] + 1721.26 * x[2] - 136.54);
    g[6] = 550 - (0.164 / (x[0] * x[1]) + 631.13 * x[2] - 54.48);

    for (int i = 0; i < numberOfOriginalConstraints; i++) {
      if (g[i] < 0.0) {
        g[i] = -g[i];
      } else {
        g[i] = 0;
      }
    }

      double sum = 0.0;
      for (double v : g) {
          sum += v;
      }
      solution.objectives()[5] = sum;

    return solution;
  }
}
