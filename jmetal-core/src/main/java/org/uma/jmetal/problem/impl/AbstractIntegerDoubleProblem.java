package org.uma.jmetal.problem.impl;

import org.uma.jmetal.problem.IntegerDoubleProblem;

import java.util.List;

@SuppressWarnings("serial")
public abstract class AbstractIntegerDoubleProblem<S>
  extends AbstractGenericProblem<S>
  implements IntegerDoubleProblem<S> {

  private int numberOfIntegerVariables ;
  private int numberOfDoubleVariables ;

  private List<Number> lowerLimit ;
  private List<Number> upperLimit ;

  /* Getters */
  public int getNumberOfDoubleVariables() {
    return numberOfDoubleVariables;
  }

  public int getNumberOfIntegerVariables() {
    return numberOfIntegerVariables;
  }

	@Override
	public Number getUpperBound(int index) {
		return upperLimit.get(index);
	}

	@Override
	public Number getLowerBound(int index) {
		return lowerLimit.get(index);
	}

  /* Setters */
  protected void setNumberOfDoubleVariables(int numberOfDoubleVariables) {
    this.numberOfDoubleVariables = numberOfDoubleVariables;
  }

  protected void setNumberOfIntegerVariables(int numberOfIntegerVariables) {
    this.numberOfIntegerVariables = numberOfIntegerVariables;
  }

  protected void setLowerLimit(List<Number> lowerLimit) {
    this.lowerLimit = lowerLimit;
  }

  protected void setUpperLimit(List<Number> upperLimit) {
    this.upperLimit = upperLimit;
  }
}
