package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/** Class representing problem Viennet4 */
@SuppressWarnings("serial")
public class Viennet4 extends AbstractDoubleProblem {
  /** Constructor. Creates a default instance of the Viennet4 problem. */
  public Viennet4() {
    setNumberOfVariables(2);
    setNumberOfObjectives(3);
    setNumberOfConstraints(3);
    setName("Viennet4");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-4.0);
      upperLimit.add(4.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
    int numberOfVariables = getNumberOfVariables();

    double[] f = new double[getNumberOfObjectives()];
    double[] x = new double[numberOfVariables];

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = solution.getVariable(i);
    }

    f[0] = (x[0] - 2.0) * (x[0] - 2.0) / 2.0 + (x[1] + 1.0) * (x[1] + 1.0) / 13.0 + 3.0;

    f[1] =
        (x[0] + x[1] - 3.0) * (x[0] + x[1] - 3.0) / 175.0
            + (2.0 * x[1] - x[0]) * (2.0 * x[1] - x[0]) / 17.0
            - 13.0;

    f[2] =
        (3.0 * x[0] - 2.0 * x[1] + 4.0) * (3.0 * x[0] - 2.0 * x[1] + 4.0) / 8.0
            + (x[0] - x[1] + 1.0) * (x[0] - x[1] + 1.0) / 27.0
            + 15.0;

    for (int i = 0; i < getNumberOfObjectives(); i++) {
      solution.setObjective(i, f[i]);
    }

    evaluateConstraints(solution);
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    double[] constraint = new double[this.getNumberOfConstraints()];

    double x1 = solution.getVariable(0);
    double x2 = solution.getVariable(1);

    constraint[0] = -x2 - (4.0 * x1) + 4.0;
    constraint[1] = x1 + 1.0;
    constraint[2] = x2 - x1 + 2.0;

    for (int i = 0; i < getNumberOfConstraints(); i++) {
      solution.setConstraint(i, constraint[i]);
    }
  }
}
