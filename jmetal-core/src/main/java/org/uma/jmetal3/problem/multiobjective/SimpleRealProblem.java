package org.uma.jmetal3.problem.multiobjective;

import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.impl.NumericSolutionImpl;
import org.uma.jmetal3.problem.impl.UnconstrainedContinuousProblemImpl;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class SimpleRealProblem extends UnconstrainedContinuousProblemImpl {
	
	/** Constructor */
	public SimpleRealProblem() {
		numberOfVariables = 5 ;
		numberOfObjectives = 2 ;
		name = "SimpleRealProblem" ;

		lowerLimit = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0} ;
		upperLimit = new Double[]{1.0, 1.0, 1.0, 1.0, 1.0} ;
	}

	public Solution createSolution() {
		Solution solution = new NumericSolutionImpl<Double>(numberOfObjectives, numberOfVariables) ;

		return solution ;
	}

	@Override
	public void evaluate(Solution solution) {	  
	}
}
