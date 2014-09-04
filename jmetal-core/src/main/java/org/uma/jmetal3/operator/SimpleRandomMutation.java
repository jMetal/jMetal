package org.uma.jmetal3.operator;

import java.util.Random;

import org.uma.jmetal3.core.Operator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal3.encoding.NumericalSolution;
import org.uma.jmetal3.problem.ContinuousProblem;

public class SimpleRandomMutation implements Operator {
  private double probability ;
  private ContinuousProblem problem ;
  
  /**  Constructor */
  public SimpleRandomMutation(ContinuousProblem problem) {
  	this.problem = problem ;
  	probability = 1.0/problem.getNumberOfVariables() ;
  }
	
	/** Execute() method */
	@Override
  public Object execute(Object object) throws JMetalException {
    if (null == object) {
      throw new JMetalException("Null parameter") ;
    } else if (!(object instanceof NumericalSolution)) {
      throw new JMetalException("Invalid parameter class") ;
    }

    NumericalSolution<Double> solution = (NumericalSolution<Double>) object;	  
    
    doMutation(probability, solution) ;
    
    return solution;
  }

	private void doMutation(double probability2, NumericalSolution<Double> solution) {
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
