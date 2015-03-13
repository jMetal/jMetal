package org.uma.jmetal.util.solutionattribute.impl;

import java.util.List;

import org.uma.jmetal.solution.Solution;


public class LocationAttribute <S extends Solution>
extends GenericSolutionAttribute<S, Integer> {
	    
	 public LocationAttribute() {}
	 
	 public LocationAttribute(List<S> source) {
		 int location = 0;
		 for (S s : source)
			 s.setAttribute(getAttributeID(), location++);
	 }
}
