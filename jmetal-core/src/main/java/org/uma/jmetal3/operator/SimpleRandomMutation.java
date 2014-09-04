package org.uma.jmetal3.operator;

import java.util.Random;

import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.core.Operator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal3.encoding.NumericalSolution;
import org.uma.jmetal3.problem.NumericProblem;
import org.uma.jmetal3.core.Solution;

public class SimpleRandomMutation implements Operator {
  private double probability ;
  private NumericProblem problem ;
  
  /**  Constructor */
  public SimpleRandomMutation(NumericProblem problem) {
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

    NumericalSolution solution = (NumericalSolution) object;	  
    
    doMutation(probability, solution) ;
    
    return solution;
  }

	// FIXME: doesn't work with integers
	private void doMutation(double probability2, NumericalSolution solution) {
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
    	Random random = new Random() ;
      if (random.nextDouble() <= probability) {
      	Number value = (Number)solution.getVariableValue(i) ; // this is useless now
      	value = problem.getLowerBound(i).doubleValue() + 
      			((problem.getUpperBound(i).doubleValue() - problem.getLowerBound(i).doubleValue())
      			* random.nextDouble()) ;
      	
      	solution.setVariableVariable(i, value) ;
      }
    }
	}

	
}
