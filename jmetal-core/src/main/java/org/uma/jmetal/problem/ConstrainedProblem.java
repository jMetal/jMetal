package org.uma.jmetal.problem;

/**
 * Interface representing problems having constraints
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public interface ConstrainedProblem<S> extends Problem<S> {

	/* Getters */
	public int getNumberOfConstraints() ;
	
	/* Methods */
  public void evaluateConstraints(S solution) ;
}
