package org.uma.jmetal.problem;

import org.uma.jmetal.solution.Solution;

/** Interface representing problems having constraints */
public interface ConstrainedProblem<S extends Solution<?>> extends Problem<S> {

	/* Getters */
	public int getNumberOfConstraints() ;
	
	/* Methods */
  public void evaluateConstraints(S solution) ;
}
