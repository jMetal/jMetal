package org.uma.jmetal3.operator.mutation.impl;

import java.util.Random;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal3.encoding.NumericSolution;
import org.uma.jmetal3.operator.mutation.MutationOperator;

public class SimpleRandomMutation implements MutationOperator<NumericSolution<Double>> {
  private double probability ;

  /**  Constructor */
  public SimpleRandomMutation(double probability) {
  	this.probability = probability ;
  }
	
	/** Execute() method */
	@Override
  public NumericSolution<Double> execute(NumericSolution<Double> object) throws JMetalException {
    if (null == object) {
      throw new JMetalException("Null parameter") ;
    }

    NumericSolution<Double> solution = (NumericSolution<Double>) object;
    
    doMutation(probability, solution) ;
    
    return solution;
  }

  /** Implements the mutation operation */
	private void doMutation(double probability, NumericSolution<Double> solution) {
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
