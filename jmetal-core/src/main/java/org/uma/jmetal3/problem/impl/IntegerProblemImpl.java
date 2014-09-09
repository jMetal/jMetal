package org.uma.jmetal3.problem.impl;

import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.problem.ContinuousProblem;
import org.uma.jmetal3.problem.IntegerProblem;

import java.util.List;

public abstract class IntegerProblemImpl<S extends Solution<? extends Integer>> extends GenericProblemImpl<S>
  implements IntegerProblem<S> {

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
