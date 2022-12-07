package org.uma.jmetal.problem.multiobjective;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/** Class representing problem Binh2 */
@SuppressWarnings("serial")
public class Binh2 extends AbstractDoubleProblem {

  /** Constructor Creates a default instance of the Binh2 problem */
  public Binh2() {
    numberOfObjectives(2);
    numberOfConstraints(2);
    name("Binh2");

    List<Double> lowerLimit = Arrays.asList(0.0, 0.0);
    List<Double> upperLimit = Arrays.asList(5.0, 3.0);

    variableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] fx = new double[solution.objectives().length];
    double[] x = new double[numberOfVariables()];
    for (int i = 0; i < numberOfVariables(); i++) {
      x[i] = solution.variables().get(i);
    }

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
