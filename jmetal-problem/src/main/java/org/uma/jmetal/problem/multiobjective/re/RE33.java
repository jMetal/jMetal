package org.uma.jmetal.problem.multiobjective.re;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;

/**
 * Class representing problem RE33. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
public class RE33 extends AbstractDoubleProblem {

  private int numberOfOriginalConstraints = 4;

  /** Constructor */
  public RE33() {
    setNumberOfVariables(4);
    setNumberOfObjectives(3);
    setNumberOfConstraints(0);
    setName("RE33");

    List<Double> lowerLimit = List.of(55.0, 75.0, 1000.0, 11.0);
    List<Double> upperLimit = List.of(80.0, 110.0, 3000.0, 20.0);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);
    double x4 = solution.variables().get(3);

    solution.objectives()[0] = 4.9 * 1e-5 * (x2 * x2 - x1 * x1) * (x4 - 1.0);
    solution.objectives()[1] = ((9.82 * 1e6) * (x2 * x2 - x1 * x1)) / (x3 * x4 * (x2 * x2 * x2 - x1 * x1 * x1));

    double[] g = new double[numberOfOriginalConstraints];
    g[0] = (x2 - x1) - 20.0;
    g[1] = 0.4 - (x3 / (3.14 * (x2 * x2 - x1 * x1)));
    g[2] =
        1.0 - (2.22 * 1e-3 * x3 * (x2 * x2 * x2 - x1 * x1 * x1)) / Math.pow((x2 * x2 - x1 * x1), 2);
    g[3] = (2.66 * 1e-2 * x3 * x4 * (x2 * x2 * x2 - x1 * x1 * x1)) / (x2 * x2 - x1 * x1) - 900.0;

    for (int i = 0; i < numberOfOriginalConstraints; i++) {
      if (g[i] < 0.0) g[i] = -g[i];
      else g[i] = 0;
    }

    solution.objectives()[2] = g[0] + g[1] + g[2] + g[3];

    return solution;
  }
}
