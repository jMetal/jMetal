package org.uma.jmetal3.problem;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.core.Solution;

public interface ConstrainedProblem extends Problem {	
	/* Getters */
	public int getNumberOfConstraints() ;
	
	/* Methods */
  public void evaluateConstraints(Solution solution) ;
}
