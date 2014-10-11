package org.uma.jmetal.problem.impl;

import org.uma.jmetal.encoding.IntegerSolution;
import org.uma.jmetal.problem.IntegerProblem;

import java.util.List;

public abstract class IntegerProblemImpl extends GenericProblemImpl<IntegerSolution>
  implements IntegerProblem {

  private List<Integer> lowerLimit ;
  private List<Integer> upperLimit ;

  /* Getters */
	@Override
	public Integer getUpperBound(int index) {
		return upperLimit.get(index);
	}

	@Override
	public Integer getLowerBound(int index) {
		return lowerLimit.get(index);
	}

  /* Setters */
  protected void setLowerLimit(List<Integer> lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  protected void setUpperLimit(List<Integer> upperLimit) {
    this.upperLimit = upperLimit;
  }
}
