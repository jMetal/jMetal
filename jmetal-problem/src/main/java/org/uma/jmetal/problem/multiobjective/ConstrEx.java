package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.Arrays;
import java.util.List;

/** Class representing problem ConstrEx */
@SuppressWarnings("serial")
public class ConstrEx extends AbstractDoubleProblem {

  /** Constructor Creates a default instance of the ConstrEx problem */
  public ConstrEx() {
    setNumberOfVariables(2);
    setNumberOfObjectives(2);
    setNumberOfConstraints(2);
    setName("ConstrEx");

    List<Double> lowerLimit = Arrays.asList(0.1, 0.0);
    List<Double> upperLimit = Arrays.asList(1.0, 5.0);

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
    double[] f = new double[getNumberOfObjectives()];
    f[0] = solution.getVariable(0);
    f[1] = (1.0 + solution.getVariable(1)) / solution.getVariable(0);

    solution.setObjective(0, f[0]);
    solution.setObjective(1, f[1]);

    this.evaluateConstraints(solution);
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution) {
    double x1 = solution.getVariable(0);
    double x2 = solution.getVariable(1);

    solution.setConstraint(0, (x2 + 9 * x1 - 6.0));
    solution.setConstraint(1, (-x2 + 9 * x1 - 1.0));
  }
}
