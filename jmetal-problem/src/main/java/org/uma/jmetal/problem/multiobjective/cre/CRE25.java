package org.uma.jmetal.problem.multiobjective.cre;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;

/**
 * Class representing problem CRE25. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class CRE25 extends AbstractDoubleProblem {

  /** Constructor */
  public CRE25() {
    setNumberOfVariables(4);
    setNumberOfObjectives(2);
    setNumberOfConstraints(1);
    setName("CRE25");

    List<Double> lowerLimit = List.of(12.0, 12.0, 12.0, 12.0);
    List<Double> upperLimit = List.of(60.0, 60.0, 60.0, 60.0);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = Math.rint(solution.getVariable(0));
    double x2 = Math.rint(solution.getVariable(1));
    double x3 = Math.rint(solution.getVariable(2));
    double x4 = Math.rint(solution.getVariable(3));

    solution.setObjective(0, Math.abs(6.931 - ((x3 / x1) * (x4 / x2))));

    double maxValue = x1;
    if (x2 > maxValue) maxValue = x2;
    if (x3 > maxValue) maxValue = x3;
    if (x4 > maxValue) maxValue = x4;
    solution.setObjective(1, maxValue);

    evaluateConstraints(solution);

    return solution;
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    double constraint = 0.5 - solution.objectives()[1] / 6.931;

    if (constraint < 0.0) {
      constraint = -constraint;
    } else {
      constraint = 0;
    }

    solution.constraints()[0] = constraint;
  }
}
