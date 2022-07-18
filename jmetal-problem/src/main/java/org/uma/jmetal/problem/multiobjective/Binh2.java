package org.uma.jmetal.problem.multiobjective;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/** Class representing problem Binh2 */
@SuppressWarnings("serial")
public class Binh2 extends AbstractDoubleProblem {

  /** Constructor Creates a default instance of the Binh2 problem */
  public Binh2() {
    setNumberOfObjectives(2);
    setNumberOfConstraints(2);
    setName("Binh2");

    var lowerLimit = Arrays.asList(0.0, 0.0);
    @NotNull List<Double> upperLimit = Arrays.asList(5.0, 3.0);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    var fx = new double[solution.objectives().length];
    var x = new double[10];
    var count = 0;
    var bound = getNumberOfVariables();
    for (var i = 0; i < bound; i++) {
      double v = solution.variables().get(i);
      if (x.length == count) x = Arrays.copyOf(x, count * 2);
      x[count++] = v;
    }
    x = Arrays.copyOfRange(x, 0, count);

    fx[0] = 4.0 * x[0] * x[0] + 4 * x[1] * x[1];
    fx[1] = (x[0] - 5.0) * (x[0] - 5.0) + (x[1] - 5.0) * (x[1] - 5.0);

    solution.objectives()[0] = fx[0];
    solution.objectives()[1] =  fx[1];

    this.evaluateConstraints(solution);
    return solution;
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    double x0 = solution.variables().get(0);
    double x1 = solution.variables().get(1);

    solution.constraints()[0] = -1.0 * (x0 - 5) * (x0 - 5) - x1 * x1 + 25.0;
    solution.constraints()[1] = (x0 - 8) * (x0 - 8) + (x1 + 3) * (x1 + 3) - 7.7;
  }
}
