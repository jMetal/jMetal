package org.uma.jmetal.util.solutionattribute.impl;

import java.util.List;
import org.uma.jmetal.solution.Solution;

/**
 * Assign to each solution in a solution list an attribute containing the position of
 * the solutions in the list.
 *
 * @author Antonio J. Nebro
 *
 * @param <S>
 */

@SuppressWarnings("serial")
public class LocationAttribute <S extends Solution<?>>
		extends GenericSolutionAttribute<S, Integer> {

	public LocationAttribute(List<S> solutionList) {
		int location = 0;
		for (S solution : solutionList)
			solution.attributes().put(getAttributeIdentifier(), location++);
	}
}
