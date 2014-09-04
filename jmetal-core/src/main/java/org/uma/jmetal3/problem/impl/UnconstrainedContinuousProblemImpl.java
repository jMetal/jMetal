package org.uma.jmetal3.problem.impl;

import org.uma.jmetal3.problem.ContinuousProblem;
import org.uma.jmetal3.problem.UnconstrainedProblem;

public abstract class UnconstrainedContinuousProblemImpl 
  implements ContinuousProblem, UnconstrainedProblem {

	protected int numberOfVariables ;
	protected int numberOfObjectives ;
	protected String name ;

	protected Double []lowerLimit ;
	protected Double []upperLimit ;
	
	@Override
	public int getNumberOfVariables() {
		return numberOfVariables ;
	}

	@Override
	public int getNumberOfObjectives() {
		return numberOfObjectives ;
	}

	@Override
	public String getName() {
		return name ;
	}

	@Override
	public Double getUpperBound(int index) {
		return upperLimit[index] ;
	}

	@Override
	public Double getLowerBound(int index) {
		return lowerLimit[index] ;
	}
}
