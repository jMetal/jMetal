package org.uma.jmetal3.problem.impl;

import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.problem.ContinuousProblem;

import java.util.ArrayList;
import java.util.List;

public abstract class ContinuousProblemImpl<T extends Number>
        extends GenericProblemImpl implements ContinuousProblem<T> {

  private List<T> lowerLimit ;
  private List<T> upperLimit ;

  /* Getters */
	@Override
	public T getUpperBound(int index) {
		return upperLimit.get(index);
	}

	@Override
	public T getLowerBound(int index) {
		return lowerLimit.get(index);
	}

  /* Setters */
  protected void setLowerLimit(List<T> lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  protected void setUpperLimit(List<T> upperLimit) {
    this.upperLimit = upperLimit;
  }
}
