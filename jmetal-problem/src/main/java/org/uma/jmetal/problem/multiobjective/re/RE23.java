package org.uma.jmetal.problem.multiobjective.re;

import java.util.List;
import java.util.stream.IntStream;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem RE23. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
public class RE23 extends AbstractDoubleProblem {

  private int numberOfOriginalConstraints = 3;

  /** Constructor */
  public RE23() {
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("RE23");

    List<Double> lowerLimit = List.of(1.0, 1.0, 10.0, 10.0);
    List<Double> upperLimit = List.of(100.0, 100.0, 200.0, 240.0);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = 0.0625 * Math.rint(solution.variables().get(0));
    double x2 = 0.0625 * Math.rint(solution.variables().get(1));
    double x3 = solution.variables().get(2);
    double x4 = solution.variables().get(3);

    double[] g = new double[numberOfOriginalConstraints];
    g[0] = x1 - (0.0193 * x3);
    g[1] = x2 - (0.00954 * x3);
    g[2] = (Math.PI * x3 * x3 * x4) + ((4.0 / 3.0) * (Math.PI * x3 * x3 * x3)) - 1296000;

    for (int i = 0; i < numberOfOriginalConstraints; i++) {
      if (g[i] < 0.0) g[i] = -g[i];
      else g[i] = 0;
    }
    solution.objectives()[0] =
            (0.6224 * x1 * x3 * x4)
            + (1.7781 * x2 * x3 * x3)
            + (3.1661 * x1 * x1 * x4)
            + (19.84 * x1 * x1 * x3);
    solution.objectives()[1] = IntStream.of(0, 1, 2).mapToDouble(i -> g[i]).sum();

    return solution;
  }
}
