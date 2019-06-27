package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionUtils;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import java.util.Arrays;
import java.util.List;

/** Class representing problem Binh2 */
@SuppressWarnings("serial")
public class Binh2 extends AbstractDoubleProblem implements ConstrainedProblem<DoubleSolution> {

  /**
   * Constructor
   * Creates a default instance of the Binh2 problem
   */
  public Binh2() {
    setNumberOfVariables(2);
    setNumberOfObjectives(2);
    setNumberOfConstraints(2);
    setName("Binh2");

    List<Double> lowerLimit = Arrays.asList(0.0, 0.0) ;
    List<Double> upperLimit = Arrays.asList(5.0, 3.0) ;

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
    double[] fx = new double[getNumberOfObjectives()];
    double[] x = new double[getNumberOfVariables()];
    for (int i = 0; i < getNumberOfVariables(); i++) {
      x[i] = solution.getVariableValue(i) ;
    }

    fx[0] = 4.0 * x[0] * x[0] + 4 * x[1] * x[1];
    fx[1] = (x[0] - 5.0) * (x[0] - 5.0) + (x[1] - 5.0) * (x[1] - 5.0);

    solution.setObjective(0, fx[0]);
    solution.setObjective(1, fx[1]);

    this.evaluateConstraints(solution);
  }

  /** EvaluateConstraints() method */
  @Override
  public void evaluateConstraints(DoubleSolution solution)  {
    double[] constraint = new double[this.getNumberOfConstraints()];

    double x0 = solution.getVariableValue(0) ;
    double x1 = solution.getVariableValue(1) ;

    constraint[0] = -1.0 * (x0 - 5) * (x0 - 5) - x1 * x1 + 25.0;
    constraint[1] = (x0 - 8) * (x0 - 8) + (x1 + 3) * (x1 + 3) - 7.7;

    SolutionUtils.setConstraintAttributes(solution, constraint);
  }
}
