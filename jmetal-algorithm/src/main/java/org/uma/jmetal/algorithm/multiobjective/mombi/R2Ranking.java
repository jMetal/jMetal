package org.uma.jmetal.algorithm.multiobjective.mombi;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

public class R2Ranking<S extends Solution<?>>
extends GenericSolutionAttribute<S, Integer> implements Ranking<S>{
	
	
	
	private List<List<Double>> weightVectors;
	
	/**
	 * Creates a R2Ranking class where the vectors are default generated
	 * @param size
	 */
	public R2Ranking(int size) {
		
	}
	
	public R2Ranking(List<List<Double>> weightVectors) {
		this.weightVectors = weightVectors;
	}
	
	@Override
	public Ranking<S> computeRanking(List<S> solutionList) {
		List<Pair<S,R2SolutionData>> population = new ArrayList<>(solutionList.size());
		
		for (S solution: solutionList) 
			population.add(Pair.of(solution,new R2SolutionData()));
		
		
		
		return null;
	}

	@Override
	public List<S> getSubfront(int rank) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfSubfronts() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	private class R2SolutionData {
		int rank 		= Integer.MAX_VALUE;
		double utility	= Double.POSITIVE_INFINITY;
		double alpha	= 0.0;					
	}

}
