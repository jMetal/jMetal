package org.uma.jmetal3.operator.impl;

import java.util.Random;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.NumericSolution;
import org.uma.jmetal3.operator.IndividualOperator;
import org.uma.jmetal3.problem.ContinuousProblem;

public class SimpleRandomMutation implements IndividualOperator<NumericSolution<Double>, NumericSolution<Double>> {
  private double probability ;
  private ContinuousProblem problem ;
  
  /**  Constructor */
  public SimpleRandomMutation(ContinuousProblem problem) {
  	this.problem = problem ;
  	probability = 1.0/problem.getNumberOfVariables() ;
  }
	
	/** Execute() method */
	@Override
  public NumericSolution execute(NumericSolution object) throws JMetalException {
    if (null == object) {
      throw new JMetalException("Null parameter") ;
    }

    NumericSolution<Double> solution = (NumericSolution<Double>) object;
    
    doMutation(probability, solution) ;
    
    return solution;
  }

	private void doMutation(double probability2, NumericSolution<Double> solution) {
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
    	Random random = new Random() ;
      if (random.nextDouble() <= probability) {
      	Double value = problem.getLowerBound(i) + 
      			((problem.getUpperBound(i) - problem.getLowerBound(i)) * random.nextDouble()) ;
      	
      	solution.setVariableVariable(i, value) ;
      }
    }
	}

}
