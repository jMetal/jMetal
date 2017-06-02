package org.uma.jmetal.problem.multiobjective;

import org.uma.jmetal.problem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem Viennet2
 */
@SuppressWarnings("serial")
public class Viennet2 extends AbstractDoubleProblem {
  
 /** 
  * Constructor.
  * Creates a default instance of the Viennet2 problem
  */
  public Viennet2() {
    setNumberOfVariables(2);
    setNumberOfObjectives(3);
    setNumberOfConstraints(0);
    setName("Viennet2") ;

    List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables()) ;
    List<Double> upperLimit = new ArrayList<>(getNumberOfVariables()) ;

    for (int i = 0; i < getNumberOfVariables(); i++) {
      lowerLimit.add(-4.0);
      upperLimit.add(4.0);
    }

    setLowerLimit(lowerLimit);
    setUpperLimit(upperLimit);
  }

  /** Evaluate() method */
  @Override
  public void evaluate(DoubleSolution solution) {
    int numberOfVariables = getNumberOfVariables() ;

    double[] f = new double[getNumberOfObjectives()];
    double[] x = new double[numberOfVariables] ;

    for (int i = 0; i < numberOfVariables; i++) {
      x[i] = solution.getVariableValue(i) ;
    }
        
    // First function
    f[0] = (x[0]-2)*(x[0]-2)/2.0 + (x[1]+1)*(x[1]+1)/13.0 + 3.0 ;

    // Second function
    f[1] = (x[0]+x[1]-3.0)*(x[0]+x[1]-3.0)/36.0 +
           (-x[0]+x[1]+2.0)*(-x[0]+x[1]+2.0)/8.0 - 17.0;

    // Third function
    f[2] = (x[0]+2*x[1]-1)*(x[0]+2*x[1]-1)/175.0 +
                          (2*x[1]-x[0])*(2*x[1]-x[0])/17.0 - 13.0 ;        
        
    for (int i = 0; i < getNumberOfObjectives(); i++)
      solution.setObjective(i,f[i]);        
  }
}


