package org.uma.jmetal3.problem;

import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.core.Solution;

/** Interface representing problems having constraints */
public interface ConstrainedProblem extends Problem {	
	/* Getters */
	public int getNumberOfConstraints() ;
	
	/* Methods */
  public void evaluateConstraints(Solution solution) ;
}
