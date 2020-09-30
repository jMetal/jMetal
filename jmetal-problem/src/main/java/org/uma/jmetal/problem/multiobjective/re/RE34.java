package org.uma.jmetal.problem.multiobjective.re;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem RE34. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
public class RE34 extends AbstractDoubleProblem {

  /** Constructor */
  public RE34() {
    setNumberOfVariables(4);
    setNumberOfObjectives(3);
    setNumberOfConstraints(0);
    setName("RE34");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(1.0);
      upperLimit.add(3.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.getVariable(0);
    double x2 = solution.getVariable(1);
    double x3 = solution.getVariable(2);
    double x4 = solution.getVariable(3);
    double x5 = solution.getVariable(4);

    solution.setObjective(
        0,
        1640.2823
            + (2.3573285 * x1)
            + (2.3220035 * x2)
            + (4.5688768 * x3)
            + (7.7213633 * x4)
            + (4.4559504 * x5));
    solution.setObjective(
        1,
        6.5856
            + (1.15 * x1)
            - (1.0427 * x2)
            + (0.9738 * x3)
            + (0.8364 * x4)
            - (0.3695 * x1 * x4)
            + (0.0861 * x1 * x5)
            + (0.3628 * x2 * x4)
            - (0.1106 * x1 * x1)
            - (0.3437 * x3 * x3)
            + (0.1764 * x4 * x4));
    solution.setObjective(
        2,
        -0.0551
            + (0.0181 * x1)
            + (0.1024 * x2)
            + (0.0421 * x3)
            - (0.0073 * x1 * x2)
            + (0.024 * x2 * x3)
            - (0.0118 * x2 * x4)
            - (0.0204 * x3 * x4)
            - (0.008 * x3 * x5)
            - (0.0241 * x2 * x2)
            + (0.0109 * x4 * x4));

    return solution;
  }
}
