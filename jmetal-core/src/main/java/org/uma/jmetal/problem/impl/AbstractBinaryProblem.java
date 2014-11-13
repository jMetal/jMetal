package org.uma.jmetal.problem.impl;

import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.problem.BinaryProblem;

public abstract class AbstractBinaryProblem extends AbstractGenericProblem<BinarySolution>
  implements BinaryProblem {

  protected abstract int getBitsPervariable(int index) ;

  @Override
  public int getNumberOfBits(int index) {
    return getBitsPervariable(index) ;
  }
  
  @Override
  public int getTotalNumberOfBits() {
  	int count = 0 ;
  	for (int i = 0; i < this.getNumberOfVariables(); i++) {
  		count += this.getBitsPervariable(i) ;
  	}
  	
  	return count ;
  }
}
