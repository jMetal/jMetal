package org.uma.jmetal3.operator;

import java.util.Random;

import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal3.core.NumericalSolution;
import org.uma.jmetal3.core.Solution;

public class SimpleRandomMutation extends Operator {
  private double probability ;
  private Problem problem ;
  
  /**  Constructor */
  public SimpleRandomMutation(Problem problem) {
  	this.problem = problem ;
  	probability = 1.0/problem.getNumberOfVariables() ;
  }
	
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

	private void doMutation(double probability2, NumericalSolution solution) {
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
    	Random random = new Random() ;
      if (random.nextDouble() <= probability) {
      	Number value = (Number)solution.getVariable(i) ; // this is useless now
      	value = problem.getLowerLimit(i) + ((problem.getUpperLimit(i) - problem.getLowerLimit(i))
      			* random.nextDouble()) ;
      	
      	solution.setVariable(i, value) ;
      }
    }
	}

	
}
