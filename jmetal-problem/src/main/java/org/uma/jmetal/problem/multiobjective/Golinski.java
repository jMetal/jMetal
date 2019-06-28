package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.Arrays;
import java.util.List;

/** Class representing problem Golinski. */
@SuppressWarnings("serial")
public class Golinski extends AbstractDoubleProblem {

  /** Constructor. Creates a default instance of the Golinski problem. */
  public Golinski() {
    setNumberOfVariables(7);
    setNumberOfObjectives(2);
    setNumberOfConstraints(11);
    setName("Golinski");

    List<Double> lowerLimit = Arrays.asList(2.6, 0.7, 17.0, 7.3, 7.3, 2.9, 5.0);
    List<Double> upperLimit = Arrays.asList(3.6, 0.8, 28.0, 8.3, 8.3, 3.9, 5.5);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
    double x1, x2, x3, x4, x5, x6, x7;
    x1 = solution.getVariable(0);
    x2 = solution.getVariable(1);
    x3 = solution.getVariable(2);
    x4 = solution.getVariable(3);
    x5 = solution.getVariable(4);
    x6 = solution.getVariable(5);
    x7 = solution.getVariable(6);

    double f1 =
        0.7854 * x1 * x2 * x2 * ((10 * x3 * x3) / 3.0 + 14.933 * x3 - 43.0934)
            - 1.508 * x1 * (x6 * x6 + x7 * x7)
            + 7.477 * (x6 * x6 * x6 + x7 * x7 * x7)
            + 0.7854 * (x4 * x6 * x6 + x5 * x7 * x7);

    double aux = 745.0 * x4 / (x2 * x3);
    double f2 = Math.sqrt((aux * aux) + 1.69e7) / (0.1 * x6 * x6 * x6);

    solution.setObjective(0, f1);
    solution.setObjective(1, f2);

    evaluateConstraints(solution);
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    double[] constraint = new double[this.getNumberOfConstraints()];
    double x1, x2, x3, x4, x5, x6, x7;

    x1 = solution.getVariable(0);
    x2 = solution.getVariable(1);
    x3 = solution.getVariable(2);
    x4 = solution.getVariable(3);
    x5 = solution.getVariable(4);
    x6 = solution.getVariable(5);
    x7 = solution.getVariable(6);

    constraint[0] = -((1.0 / (x1 * x2 * x2 * x3)) - (1.0 / 27.0));
    constraint[1] = -((1.0 / (x1 * x2 * x2 * x3 * x3)) - (1.0 / 397.5));
    constraint[2] = -((x4 * x4 * x4) / (x2 * x3 * x3 * x6 * x6 * x6 * x6) - (1.0 / 1.93));
    constraint[3] = -((x5 * x5 * x5) / (x2 * x3 * x7 * x7 * x7 * x7) - (1.0 / 1.93));
    constraint[4] = -(x2 * x3 - 40.0);
    constraint[5] = -((x1 / x2) - 12.0);
    constraint[6] = -(5.0 - (x1 / x2));
    constraint[7] = -(1.9 - x4 + 1.5 * x6);
    constraint[8] = -(1.9 - x5 + 1.1 * x7);

    double aux = 745.0 * x4 / (x2 * x3);
    double f2 = java.lang.Math.sqrt((aux * aux) + 1.69e7) / (0.1 * x6 * x6 * x6);
    constraint[9] = -(f2 - 1300);
    double a = 745.0 * x5 / (x2 * x3);
    double b = 1.575e8;
    constraint[10] = -(java.lang.Math.sqrt(a * a + b) / (0.1 * x7 * x7 * x7) - 1100.0);

    for (int i = 0; i < getNumberOfConstraints(); i++) {
      solution.setConstraint(i, constraint[i]);
    }
  }
}
