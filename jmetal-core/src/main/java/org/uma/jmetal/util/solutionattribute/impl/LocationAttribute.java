package org.uma.jmetal.util.solutionattribute.impl;

import org.uma.jmetal.solution.Solution;

import java.util.List;

@SuppressWarnings("serial")
public class LocationAttribute <S extends Solution<?>>
		extends GenericSolutionAttribute<S, Integer> {

	public LocationAttribute(List<S> source) {
		int location = 0;
		for (S s : source)
			s.setAttribute(getAttributeIdentifier(), location++);
	}
}
