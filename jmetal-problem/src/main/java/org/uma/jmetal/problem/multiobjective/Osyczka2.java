package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import java.util.Arrays;
import java.util.List;

/**
 * Class representing problem Oyczka2
 */
@SuppressWarnings("serial")
public class Osyczka2 extends AbstractDoubleProblem implements ConstrainedProblem<DoubleSolution> {
  public OverallConstraintViolation<DoubleSolution> overallConstraintViolationDegree ;
  public NumberOfViolatedConstraints<DoubleSolution> numberOfViolatedConstraints ;

 /**
  * Constructor.
  * Creates a default instance of the Osyczka2 problem.
  */
  public Osyczka2() {
    setNumberOfVariables(6);
    setNumberOfObjectives(2);
    setNumberOfConstraints(6);
    setName("Osyczka2") ;

    List<Double> lowerLimit = Arrays.asList(0.0, 0.0, 1.0, 0.0, 1.0, 0.0) ;
    List<Double> upperLimit = Arrays.asList(10.0, 10.0, 5.0, 6.0, 5.0, 10.0) ;

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);

    overallConstraintViolationDegree = new OverallConstraintViolation<DoubleSolution>() ;
    numberOfViolatedConstraints = new NumberOfViolatedConstraints<DoubleSolution>() ;
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution)  {
    double [] fx = new double[getNumberOfObjectives()];
    
    double x1,x2,x3,x4,x5,x6;
    x1 = solution.getVariableValue(0);
    x2 = solution.getVariableValue(1);
    x3 = solution.getVariableValue(2);
    x4 = solution.getVariableValue(3);
    x5 = solution.getVariableValue(4);
    x6 = solution.getVariableValue(5);

    fx[0] = - (25.0*(x1-2.0)*(x1-2.0) +
                  (x2-2.0)*(x2-2.0) + 
                  (x3-1.0)*(x3-1.0) + 
                  (x4-4.0)*(x4-4.0)+
                  (x5-1.0)*(x5-1.0));
                
    fx[1] = x1*x1 + x2*x2 + x3*x3 + x4*x4 + x5*x5 + x6*x6;
    
    solution.setObjective(0,fx[0]);
    solution.setObjective(1,fx[1]);
  }

  /** EvaluateConstraints() method */
  @Override
  public void evaluateConstraints(DoubleSolution solution)  {
    double[] constraint = new double[this.getNumberOfConstraints()];

    double x1,x2,x3,x4,x5,x6;
    x1 = solution.getVariableValue(0);
    x2 = solution.getVariableValue(1);
    x3 = solution.getVariableValue(2);
    x4 = solution.getVariableValue(3);
    x5 = solution.getVariableValue(4);
    x6 = solution.getVariableValue(5);

    constraint[0] = (x1 + x2)/2.0 - 1.0;
    constraint[1] = (6.0 - x1 - x2)/6.0;
    constraint[2] = (2.0 - x2 + x1)/2.0;
    constraint[3] = (2.0 - x1 + 3.0*x2)/2.0;
    constraint[4] = (4.0 - (x3-3.0)*(x3-3.0) - x4)/4.0;
    constraint[5] = ((x5-3.0)*(x5-3.0) +x6 - 4.0)/4.0;

    double overallConstraintViolation = 0.0;
    int violatedConstraints = 0;
    for (int i = 0; i < getNumberOfConstraints(); i++) {
      if (constraint[i]<0.0){
        overallConstraintViolation+=constraint[i];
        violatedConstraints++;
      }
    }

    overallConstraintViolationDegree.setAttribute(solution, overallConstraintViolation);
    numberOfViolatedConstraints.setAttribute(solution, violatedConstraints);
  }
}

