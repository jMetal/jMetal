package org.uma.jmetal3.problem.multiobjective;

import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal3.encoding.impl.NumericalSolutionImpl;
import org.uma.jmetal3.problem.NumericProblem;
import org.uma.jmetal3.problem.UnconstrainedProblem;
import org.uma.jmetal3.problem.impl.UnconstrainedNumericProblemImpl;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class SimpleRealProblem extends UnconstrainedNumericProblemImpl {
	
	/** Constructor */
	public SimpleRealProblem() {
		numberOfVariables = 5 ;
		numberOfObjectives = 2 ;
		name = "SimpleRealProblem" ;

		lowerLimit = new Double[]{0.0, 0.0, 0.0, 0.0, 0.0} ;
		upperLimit = new Double[]{1.0, 1.0, 1.0, 1.0, 1.0} ;
	}

	public Solution createSolution() {
		Solution solution = new NumericalSolutionImpl<Double>(numberOfObjectives, numberOfVariables) ;

		return solution ;
	}

	@Override
	public void evaluate(Solution solution) {	  
	}
}
