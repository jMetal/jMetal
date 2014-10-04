package org.uma.jmetal3.problem.impl;

import org.uma.jmetal3.encoding.DoubleSolution;
import org.uma.jmetal3.problem.ContinuousProblem;

import java.util.List;
// public interface ContinuousProblem<S extends Solution<? extends Double>> extends Problem<S> {
// public abstract class GenericProblemImpl<S extends Solution<?>> implements Problem<S> {

public abstract class ContinuousProblemImpl extends GenericProblemImpl<DoubleSolution>
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
}
