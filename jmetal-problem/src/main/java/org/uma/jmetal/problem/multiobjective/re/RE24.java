package org.uma.jmetal.problem.multiobjective.re;

import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem RE24. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
public class RE24 extends AbstractDoubleProblem {

  private int numberOfOriginalConstraints = 4;

  /** Constructor */
  public RE24() {
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("RE24");

    List<Double> lowerLimit = List.of(0.5, 0.5);
    List<Double> upperLimit = List.of(4.0, 50.0);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);

    double e = 700000;
    double sigmaBMax = 700;
    double tauMax = 450;
    double deltaMax = 1.5;
    double sigmaK = (e * x1 * x1) / 100;
    double sigmaB = 4500 / (x1 * x2);
    double tau = 1800 / x2;
    double delta = (56.2 * 10000) / (e * x1 * x2 * x2);

    double[] g = new double[numberOfOriginalConstraints];

    g[0] = 1 - (sigmaB / sigmaBMax);
    g[1] = 1 - (tau / tauMax);
    g[2] = 1 - (delta / deltaMax);
    g[3] = 1 - (sigmaB / sigmaK);

    for (int i = 0; i < numberOfOriginalConstraints; i++) {
      if (g[i] < 0.0) g[i] = -g[i];
      else g[i] = 0;
    }

    solution.objectives()[0] =  x1 + (120 * x2);
      double sum = 0.0;
      for (int i : new int[]{0, 1, 2, 3}) {
          double v = g[i];
          sum += v;
      }
      solution.objectives()[1] = sum;

    return solution;
  }
}
