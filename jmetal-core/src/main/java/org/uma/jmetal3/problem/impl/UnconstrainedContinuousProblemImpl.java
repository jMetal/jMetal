package org.uma.jmetal3.problem.impl;

import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.problem.ContinuousProblem;

import java.util.ArrayList;
import java.util.List;

public abstract class UnconstrainedContinuousProblemImpl 
  implements ContinuousProblem, Problem {

	private int numberOfVariables ;
  private int numberOfObjectives ;
  private String name ;

  private List<Double> lowerLimit ;
  private List<Double> upperLimit ;

  /* Getters */
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
		return upperLimit.get(index);
	}

	@Override
	public Double getLowerBound(int index) {
		return lowerLimit.get(index);
	}

  /* Setters */
  public void setNumberOfVariables(int numberOfVariables) {
    this.numberOfVariables = numberOfVariables;
  }

  public void setNumberOfObjectives(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setLowerLimit(List<Double> lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  public void setUpperLimit(List<Double> upperLimit) {
    this.upperLimit = upperLimit;
  }
}
