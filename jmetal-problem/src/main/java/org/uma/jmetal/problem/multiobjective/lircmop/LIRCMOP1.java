package org.uma.jmetal.problem.multiobjective.lircmop;

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionUtils;

import java.util.ArrayList;
import java.util.List;

import static org.uma.jmetal.problem.ConstrainedProblem.Attributes.NUMBER_OF_VIOLATED_CONSTRAINTS;
import static org.uma.jmetal.problem.ConstrainedProblem.Attributes.OVERALL_CONSTRAINT_VIOLATION_DEGREE;

/**
 * Class representing problem LIR-CMOP1, defined in:
 * An Improved epsilon-constrained Method in MOEA/D for CMOPs with Large Infeasible Regions
 * Fan, Z., Li, W., Cai, X. et al. Soft Comput (2019). https://doi.org/10.1007/s00500-019-03794-x
 */
@SuppressWarnings("serial")
public class LIRCMOP1 extends AbstractDoubleProblem implements ConstrainedProblem<DoubleSolution> {
  /**
   * Constructor
   */

  public LIRCMOP1() {
    this(30) ;
  }
  /**
   * Constructor
   */
  public LIRCMOP1(int numberOfVariables) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(2);
    setNumberOfConstraints(2);
    setName("LIRCMOP1");

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

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

    fx[0] = x[0] + g1(x) ;
    fx[1] = 1 - x[0]*x[0] + g2(x) ;

    solution.setObjective(0, fx[0]);
    solution.setObjective(1, fx[1]);

    //evaluateConstraints(solution);
  }

  /** EvaluateConstraints() method */
  @Override
  public void evaluateConstraints(DoubleSolution solution)  {
    double[] x = new double[getNumberOfVariables()];
    for (int i = 0; i < getNumberOfVariables(); i++) {
      x[i] = solution.getVariableValue(i) ;
    }

    double[] constraint = new double[this.getNumberOfConstraints()];
    final double a = 0.51 ;
    final double b = 0.5 ;

    constraint[0] = (a - g1(x)) * (g1(x) - b) ;
    constraint[1] = (a - g2(x)) * (g2(x) - b) ;

    SolutionUtils.setConstraintAttributes(solution, constraint);
  }

  protected double g1(double[] x) {
    double result = 0.0 ;
    for (int i = 2; i < getNumberOfVariables(); i+=2) {
      result += Math.pow(x[i] - Math.sin(0.5*Math.PI*x[0]), 2.0) ;
    }

    return result ;
  }

  protected double g2(double[] x) {
    double result = 0.0 ;
    for (int i = 1; i < (getNumberOfVariables()-1) ; i+=2) {
      result += Math.pow(x[i] - Math.cos(0.5*Math.PI*x[0]), 2.0) ;
    }

    return result ;
  }
}
