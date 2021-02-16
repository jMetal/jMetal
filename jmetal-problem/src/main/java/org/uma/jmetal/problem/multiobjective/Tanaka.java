package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem Tanaka
 */
@SuppressWarnings("serial")
public class Tanaka extends AbstractDoubleProblem {
  /**
   * Constructor.
   * Creates a default instance of the problem Tanaka
   */
  public Tanaka() {
    setNumberOfVariables(2);
    setNumberOfObjectives(2);
    setNumberOfConstraints(2);
    setName("Tanaka") ;

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(10e-5);
      upperLimit.add(Math.PI);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution)  {
    solution.setObjective(0, solution.variables().get(0));
    solution.setObjective(1, solution.variables().get(1));

    this.evaluateConstraints(solution);
    return solution ;
  }

  /** EvaluateConstraints() method */
  public void evaluateConstraints(DoubleSolution solution)  {
    double[] constraint = new double[this.getNumberOfConstraints()];

    double x1 = solution.variables().get(0) ;
    double x2 = solution.variables().get(1) ;

    constraint[0] = (x1 * x1 + x2 * x2 - 1.0 - 0.1 * Math.cos(16.0 * Math.atan(x1 / x2)));
    constraint[1] = -2.0 * ((x1 - 0.5) * (x1 - 0.5) + (x2 - 0.5) * (x2 - 0.5) - 0.5);

    for (int i = 0; i < getNumberOfConstraints(); i++) {
      solution.constraints()[i] = constraint[i];
    }
  }
}
