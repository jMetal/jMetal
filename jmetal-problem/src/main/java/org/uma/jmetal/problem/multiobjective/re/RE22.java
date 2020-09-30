package org.uma.jmetal.problem.multiobjective.re;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

import static org.uma.jmetal.problem.multiobjective.re.Util.getClosestValue;

/**
 * Class representing problem RE22. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
public class RE22 extends AbstractDoubleProblem {

  private int numberOfOriginalConstraints = 2;
  private static final double[] asFeasibleIntegers = {
    0.20, 0.31, 0.40, 0.44, 0.60, 0.62, 0.79, 0.80, 0.88, 0.93, 1.0, 1.20, 1.24, 1.32, 1.40, 1.55,
    1.58, 1.60, 1.76, 1.80, 1.86, 2.0, 2.17, 2.20, 2.37, 2.40, 2.48, 2.60, 2.64, 2.79, 2.80, 3.0,
    3.08, 3, 10, 3.16, 3.41, 3.52, 3.60, 3.72, 3.95, 3.96, 4.0, 4.03, 4.20, 4.34, 4.40, 4.65, 4.74,
    4.80, 4.84, 5.0, 5.28, 5.40, 5.53, 5.72, 6.0, 6.16, 6.32, 6.60, 7.11, 7.20, 7.80, 7.90, 8.0,
    8.40, 8.69, 9.0, 9.48, 10.27, 11.0, 11.06, 11.85, 12.0, 13.0, 14.0, 15.0
  };

  /** Constructor */
  public RE22() {
    setNumberOfVariables(4);
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("RE22");

    List<Double> lowerLimit = List.of(0.2, 0.0, 0.0);
    List<Double> upperLimit = List.of(15.0, 20.0, 40.0);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = getClosestValue(asFeasibleIntegers, solution.getVariable(0));
    double x2 = solution.getVariable(1);
    double x3 = solution.getVariable(2);

    double[] g = new double[numberOfOriginalConstraints];
    g[0] = (x1 * x3) - 7.735 * ((x1 * x1) / x2) - 180.0;
    g[1] = 4.0 - (x3 / x2);

    for (int i = 0; i < numberOfOriginalConstraints; i++) {
      if (g[i] < 0.0) g[i] = -g[i];
      else g[i] = 0;
    }

    solution.setObjective(0, (29.4 * x1) + (0.6 * x2 * x3));
    solution.setObjective(1, g[0] + g[1]);

    return solution;
  }

}
