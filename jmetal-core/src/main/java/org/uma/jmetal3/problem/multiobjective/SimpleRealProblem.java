package org.uma.jmetal3.problem.multiobjective;

import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal3.encoding.impl.NumericalSolutionImpl;
import org.uma.jmetal3.problem.NumericProblem;
import org.uma.jmetal3.problem.UnconstrainedProblem;

/**
 * Created by Antonio J. Nebro on 03/09/14.
 */
public class SimpleRealProblem implements UnconstrainedProblem, NumericProblem {
	private int numberOfVariables ;
	private int numberOfObjectives ;
	private String name ;

	private Double []lowerLimit ;
	private Double []upperLimit ;

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

	@Override
	public Number getUpperBound(int index) {
		return upperLimit[index] ;
	}

	@Override
	public Number getLowerBound(int index) {
		return lowerLimit[index] ;
	}
}
