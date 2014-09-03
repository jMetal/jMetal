package org.uma.jmetal3.operator;

import org.uma.jmetal.core.Operator;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.random.PseudoRandom;
import org.uma.jmetal3.core2.NumericalSolution;
import org.uma.jmetal3.core2.Solution;

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
      if (PseudoRandom.randDouble() <= probability) {
      	Number value = (Number)solution.getVariable(i) ;
      	
      }
    }
	}

	
}
