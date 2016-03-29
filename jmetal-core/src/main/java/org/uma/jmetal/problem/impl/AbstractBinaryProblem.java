package org.uma.jmetal.problem.impl;

import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.impl.DefaultBinarySolution;

@SuppressWarnings("serial")
public abstract class AbstractBinaryProblem extends AbstractGenericProblem<BinarySolution>
  implements BinaryProblem {

  protected abstract int getBitsPerVariable(int index) ;

  @Override
  public int getNumberOfBits(int index) {
    return getBitsPerVariable(index) ;
  }
  
  @Override
  public int getTotalNumberOfBits() {
  	int count = 0 ;
  	for (int i = 0; i < this.getNumberOfVariables(); i++) {
  		count += this.getBitsPerVariable(i) ;
  	}
  	
  	return count ;
  }

  @Override
  public BinarySolution createSolution() {
    return new DefaultBinarySolution(this)  ;
  }
}
