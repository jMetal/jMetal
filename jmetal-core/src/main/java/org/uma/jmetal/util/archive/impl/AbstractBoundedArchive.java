package org.uma.jmetal.util.archive.impl;

import java.util.List;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.BoundedArchive;

public abstract class AbstractBoundedArchive<S extends Solution> implements BoundedArchive<S> {
	protected NonDominatedSolutionListArchive list;
	protected int maxSize;
	
	public AbstractBoundedArchive(int maxSize) {
		this.maxSize = maxSize;
		this.list = new NonDominatedSolutionListArchive();
	}
	
	@Override
	public boolean add(S solution) {
		boolean success = list.add(solution);
		if (success) 
			prune();

		return success;
			
	}

	@Override
	public S get(int index) {
		return getSolutionList().get(index);
	}

	@Override
	public List<S> getSolutionList() {
		return list.getSolutionList();
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public int getMaxSize() {
		return maxSize;
	}
	
	public abstract void prune();
}
