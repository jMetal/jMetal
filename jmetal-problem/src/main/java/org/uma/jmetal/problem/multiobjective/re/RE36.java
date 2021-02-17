package org.uma.jmetal.problem.multiobjective.re;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;

/**
 * Class representing problem RE36. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
public class RE36 extends AbstractDoubleProblem {

  private int numberOfOriginalConstraints = 1;

  /** Constructor */
  public RE36() {
    setNumberOfVariables(4);
    setNumberOfObjectives(3);
    setNumberOfConstraints(0);
    setName("RE36");

    List<Double> lowerLimit = List.of(12.0, 12.0, 12.0, 12.0);
    List<Double> upperLimit = List.of(60.0, 60.0, 60.0, 60.0);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = Math.rint(solution.variables().get(0));
    double x2 = Math.rint(solution.variables().get(1));
    double x3 = Math.rint(solution.variables().get(2));
    double x4 = Math.rint(solution.variables().get(3));

    solution.objectives()[0] = Math.abs(6.931 - ((x3 / x1) * (x4 / x2)));

    double maxValue = x1;
    if (x2 > maxValue) maxValue = x2;
    if (x3 > maxValue) maxValue = x3;
    if (x4 > maxValue) maxValue = x4;
    solution.objectives()[1] = maxValue;

    double[] g = new double[numberOfOriginalConstraints];
    g[0] = 0.5 - (solution.objectives()[0] / 6.931);

    for (int i = 0; i < numberOfOriginalConstraints; i++) {
      if (g[i] < 0.0) g[i] = -g[i];
      else g[i] = 0;
    }

    solution.objectives()[2] = g[0];

    return solution;
  }
}
