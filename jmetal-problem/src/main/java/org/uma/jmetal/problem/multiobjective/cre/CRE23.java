package org.uma.jmetal.problem.multiobjective.cre;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;

/**
 * Class representing problem CRE23. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class CRE23 extends AbstractDoubleProblem {

  /** Constructor */
  public CRE23() {
    setNumberOfVariables(4);
    setNumberOfObjectives(2);
    setNumberOfConstraints(4);
    setName("CRE23");

    List<Double> lowerLimit = List.of(55.0, 75.0, 1000.0, 11.0);
    List<Double> upperLimit = List.of(80.0, 110.0, 3000.0, 20.0);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double x1 = solution.getVariable(0);
    double x2 = solution.getVariable(1);
    double x3 = solution.getVariable(2);
    double x4 = solution.getVariable(3);

    solution.setObjective(0, 4.9 * 1e-5 * (x2 * x2 - x1 * x1) * (x4 - 1.0));
    solution.setObjective(1, ((9.82 * 1e6) * (x2 * x2 - x1 * x1)) / (x3 * x4 * (x2 * x2 * x2 - x1 * x1 * x1)));

    evaluateConstraints(solution);

    return solution;
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    double[] constraint = new double[this.getNumberOfConstraints()];
    double x1, x2, x3, x4;

    x1 = solution.getVariable(0);
    x2 = solution.getVariable(1);
    x3 = solution.getVariable(2);
    x4 = solution.getVariable(3);

    constraint[0] = (x2 - x1) - 20.0;
    constraint[1] = 0.4 - (x3 / (3.14 * (x2 * x2 - x1 * x1)));
    constraint[2] = 1.0 - (2.22 * 1e-3 * x3 * (x2 * x2 * x2 - x1 * x1 * x1)) / Math.pow((x2 * x2 - x1 * x1), 2);
    constraint[3] = (2.66 * 1e-2 * x3 * x4 * (x2 * x2 * x2 - x1 * x1 * x1)) / (x2 * x2 - x1 * x1) - 900.0;

    for (int i = 0; i < getNumberOfConstraints(); i++) {
      if (constraint[i] < 0.0) {
        constraint[i] = -constraint[i];
      } else {
        constraint[i] = 0;
      }
    }

    for (int i = 0; i < getNumberOfConstraints(); i++) {
      solution.setConstraint(i, constraint[i]);
    }
  }
}
