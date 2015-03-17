package org.uma.jmetal.measure.impl;

import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 *
 * @param <S>
 *            the list of solutions
 */
public class SolutionListMeasure <S extends List<? extends Solution>> extends
		SimplePushMeasure<S> {

	public SolutionListMeasure() {
		super("Solution list measure",
				"Provide a list of solutions");
	}

	/**.
	 * 
	 * @param solutionList
	 *            the solution evaluated
	 */
	@Override public void push(S solutionList) {
		super.push(solutionList);
	}
}
