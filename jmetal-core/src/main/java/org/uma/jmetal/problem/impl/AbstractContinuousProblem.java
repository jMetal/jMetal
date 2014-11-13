package org.uma.jmetal.problem.impl;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.problem.ContinuousProblem;
import org.uma.jmetal.solution.impl.GenericDoubleSolution;

import java.util.List;

public abstract class AbstractContinuousProblem extends AbstractGenericProblem<DoubleSolution>
  implements ContinuousProblem {

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

  @Override
  public DoubleSolution createSolution() {
    return new GenericDoubleSolution(this)  ;
  }
}
