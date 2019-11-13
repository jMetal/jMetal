package org.uma.jmetal.problem.impl;

import org.uma.jmetal.problem.IntegerProblem;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.solution.impl.DefaultIntegerSolution;

import java.util.List;

@SuppressWarnings("serial")
public abstract class AbstractIntegerProblem extends AbstractGenericProblem<IntegerSolution>
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

  @Override
  public IntegerSolution createSolution() {
    return new DefaultIntegerSolution(this) ;
  }

}
