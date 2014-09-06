package org.uma.jmetal3.problem.impl;

import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.problem.ContinuousProblem;

import java.util.ArrayList;
import java.util.List;

public abstract class UnconstrainedContinuousProblemImpl
        extends GenericProblemImpl implements ContinuousProblem {

  private List<Double> lowerLimit ;
  private List<Double> upperLimit ;

  /* Getters */
	@Override
	public Double getUpperBound(int index) {
		return upperLimit.get(index);
	}

	@Override
	public Double getLowerBound(int index) {
		return lowerLimit.get(index);
	}

  /* Setters */
  protected void setLowerLimit(List<Double> lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  protected void setUpperLimit(List<Double> upperLimit) {
    this.upperLimit = upperLimit;
  }
}
