package org.uma.jmetal3.operator.mutation.impl;

import java.util.Random;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal3.encoding.DoubleSolution;
import org.uma.jmetal3.operator.mutation.MutationOperator;

public class SimpleRandomMutation implements MutationOperator<DoubleSolution> {
  private double probability ;

  /**  Constructor */
  public SimpleRandomMutation(double probability) {
  	this.probability = probability ;
  }
	
	/** Execute() method */
	@Override
  public DoubleSolution execute(DoubleSolution solution) throws JMetalException {
    if (null == solution) {
      throw new JMetalException("Null parameter") ;
    }

    doMutation(probability, solution) ;
    
    return solution;
  }

  /** Implements the mutation operation */
	private void doMutation(double probability, DoubleSolution solution) {
    for (int i = 0; i < solution.getNumberOfVariables(); i++) {
    	Random random = new Random() ;
      if (random.nextDouble() <= probability) {
      	Double value = solution.getLowerBound(i) +
      			((solution.getUpperBound(i) - solution.getLowerBound(i)) * random.nextDouble()) ;
      	
      	solution.setVariableValue(i, value) ;
      }
    }
	}

}
