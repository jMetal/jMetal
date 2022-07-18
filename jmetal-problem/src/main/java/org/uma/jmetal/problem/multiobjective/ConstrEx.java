package org.uma.jmetal.problem.multiobjective;

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/** Class representing problem ConstrEx */
@SuppressWarnings("serial")
public class ConstrEx extends AbstractDoubleProblem {

  /** Constructor Creates a default instance of the ConstrEx problem */
  public ConstrEx() {
    setNumberOfObjectives(2);
    setNumberOfConstraints(2);
    setName("ConstrEx");

    var lowerLimit = Arrays.asList(0.1, 0.0);
    @NotNull List<Double> upperLimit = Arrays.asList(1.0, 5.0);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public @NotNull DoubleSolution evaluate(@NotNull DoubleSolution solution) {
    var f = new double[solution.objectives().length];
    f[0] = solution.variables().get(0);
    f[1] = (1.0 + solution.variables().get(1)) / solution.variables().get(0);

    solution.objectives()[0] = f[0];
    solution.objectives()[1] = f[1];

    this.evaluateConstraints(solution);

    return solution ;
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    double x1 = solution.variables().get(0);
    double x2 = solution.variables().get(1);

    solution.constraints()[0] = (x2 + 9 * x1 - 6.0);
    solution.constraints()[1] = (-x2 + 9 * x1 - 1.0);
  }
}
