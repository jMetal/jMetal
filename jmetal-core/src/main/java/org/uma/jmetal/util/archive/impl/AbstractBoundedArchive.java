package org.uma.jmetal.util.archive.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.BoundedArchive;

import java.util.List;

public abstract class AbstractBoundedArchive<S extends Solution<?>> implements BoundedArchive<S> {
	protected NonDominatedSolutionListArchive<S> list;
	protected int maxSize;
	
	public AbstractBoundedArchive(int maxSize) {
		this.maxSize = maxSize;
		this.list = new NonDominatedSolutionListArchive<S>();
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
