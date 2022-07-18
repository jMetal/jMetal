package org.uma.jmetal.problem.multiobjective;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/** Class representing problem Oyczka2 */
@SuppressWarnings("serial")
public class Osyczka2 extends AbstractDoubleProblem {

  /** Constructor. Creates a default instance of the Osyczka2 problem. */
  public Osyczka2() {
    setNumberOfObjectives(2);
    setNumberOfConstraints(6);
    setName("Osyczka2");

    @NotNull List<Double> lowerLimit = Arrays.asList(0.0, 0.0, 1.0, 0.0, 1.0, 0.0);
    var upperLimit = Arrays.asList(10.0, 10.0, 5.0, 6.0, 5.0, 10.0);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(@NotNull DoubleSolution solution) {
    var fx = new double[solution.objectives().length];

    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);
    double x4 = solution.variables().get(3);
    double x5 = solution.variables().get(4);
    double x6 = solution.variables().get(5);

    fx[0] =
        -(25.0 * (x1 - 2.0) * (x1 - 2.0)
            + (x2 - 2.0) * (x2 - 2.0)
            + (x3 - 1.0) * (x3 - 1.0)
            + (x4 - 4.0) * (x4 - 4.0)
            + (x5 - 1.0) * (x5 - 1.0));

    fx[1] = x1 * x1 + x2 * x2 + x3 * x3 + x4 * x4 + x5 * x5 + x6 * x6;

    solution.objectives()[0] = fx[0];
    solution.objectives()[1] = fx[1];

    evaluateConstraints(solution);
    return solution ;
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    var constraint = new double[this.getNumberOfConstraints()];

    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);
    double x3 = solution.variables().get(2);
    double x4 = solution.variables().get(3);
    double x5 = solution.variables().get(4);
    double x6 = solution.variables().get(5);

    constraint[0] = (x1 + x2) / 2.0 - 1.0;
    constraint[1] = (6.0 - x1 - x2) / 6.0;
    constraint[2] = (2.0 - x2 + x1) / 2.0;
    constraint[3] = (2.0 - x1 + 3.0 * x2) / 2.0;
    constraint[4] = (4.0 - (x3 - 3.0) * (x3 - 3.0) - x4) / 4.0;
    constraint[5] = ((x5 - 3.0) * (x5 - 3.0) + x6 - 4.0) / 4.0;

    for (var i = 0; i < getNumberOfConstraints(); i++) {
      solution.constraints()[i] = constraint[i];
    }
  }
}
