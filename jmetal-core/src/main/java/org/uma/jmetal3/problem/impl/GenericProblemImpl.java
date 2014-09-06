package org.uma.jmetal3.problem.impl;

import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.problem.ContinuousProblem;

import java.util.List;

public abstract class GenericProblemImpl implements Problem {

	private int numberOfVariables ;
  private int numberOfObjectives ;
  private String name ;

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

  /* Setters */
  protected void setNumberOfVariables(int numberOfVariables) {
    this.numberOfVariables = numberOfVariables;
  }

  protected void setNumberOfObjectives(int numberOfObjectives) {
    this.numberOfObjectives = numberOfObjectives;
  }

  protected void setName(String name) {
    this.name = name;
  }
}
