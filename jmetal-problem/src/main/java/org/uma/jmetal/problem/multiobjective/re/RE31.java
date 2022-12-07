package org.uma.jmetal.problem.multiobjective.re;

import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem RE31. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
public class RE31 extends AbstractDoubleProblem {

  private int numberOfOriginalConstraints = 3;

  /** Constructor */
  public RE31() {
    numberOfObjectives(3);
    numberOfConstraints(0);
    name("RE31");

    List<Double> lowerLimit = List.of(0.00001, 0.00001, 1.0);
    List<Double> upperLimit = List.of(100.0, 100.0, 3.0);

    variableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);

    solution.objectives()[0] = x1 * Math.sqrt(16.0 + (x3 * x3)) + x2 * Math.sqrt(1.0 + x3 * x3);
    solution.objectives()[1] = (20.0 * Math.sqrt(16.0 + (x3 * x3))) / (x1 * x3);

    double[] g = new double[numberOfOriginalConstraints];
    g[0] = 0.1 - solution.objectives()[0];
    g[1] = 100000.0 - solution.objectives()[1];
    g[2] = 100000 - ((80.0 * Math.sqrt(1.0 + x3 * x3)) / (x3 * x2));

    for (int i = 0; i < numberOfOriginalConstraints; i++) {
      if (g[i] < 0.0) g[i] = -g[i];
      else g[i] = 0;
    }

    solution.objectives()[2] = g[0] + g[1] + g[2];

    return solution;
  }
}
