package org.uma.jmetal.util.solutionattribute.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;
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
		var location = 0;
		for (@NotNull S solution : solutionList)
			solution.attributes().put(getAttributeIdentifier(), location++);
	}
}
