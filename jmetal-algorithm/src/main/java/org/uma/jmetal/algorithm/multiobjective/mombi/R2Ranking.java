package org.uma.jmetal.algorithm.multiobjective.mombi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

public class R2Ranking<S extends Solution<?>> implements Ranking<S> {

	private AbstractUtilityFunctionsSet<S> utilityFunctions;
	private List<List<S>> rankedSubpopulations;
	private int numberOfRanks = 0;

	public R2Ranking(AbstractUtilityFunctionsSet<S> utilityFunctions) {
		this.utilityFunctions = utilityFunctions;
	}

	@Override
	public Ranking<S> computeRanking(List<S> population) {
		
		R2RankingAttribute<S> attribute = new R2RankingAttribute<>();

		for (S solution : population)
			solution.setAttribute(attribute, new R2SolutionData());

		for (int i = 0; i < this.utilityFunctions.getSize(); i++) {
			for (S p : population) {
				attribute.getAttribute(p).alpha = this.utilityFunctions.evaluate(p, i);

				if (((R2SolutionData) p.getAttribute(attribute.getAttributeID())).alpha < ((R2SolutionData) p
						.getAttribute(getAttributeID())).utility)
					((R2SolutionData) p.getAttribute(attribute.getAttributeID())).utility = ((R2SolutionData) p
							.getAttribute(getAttributeID())).alpha;

			}

			Collections.sort(population, new Comparator<S>() {
				@Override
				public int compare(S o1, S o2) {
					R2RankingAttribute<S> attribute = new R2RankingAttribute<>();
					R2SolutionData data1 = (R2SolutionData) attribute.getAttribute(o1);
					R2SolutionData data2 = (R2SolutionData) attribute.getAttribute(o2);
										
					if (data1.alpha < data2.alpha)
						return -1;
					else if (data1.alpha > data2.alpha)
						return 1;
					else
						return 0;
				}
			});

			int rank = 1;
			for (S p : population) {
				R2SolutionData r2Data = attribute.getAttribute(p);
				if (rank < r2Data.rank) {
					p.setAttribute(attribute.getAttributeID(), rank);
					numberOfRanks = Math.max(numberOfRanks, rank);
				}
				rank = rank + 1;
			}
		}

		Map<Integer, List<S>> fronts = new TreeMap<>(); // sorted on key
		for (S p : population) {
			R2SolutionData r2Data = attribute.getAttribute(p);
			if (fronts.get(r2Data.rank) == null)
				fronts.put(r2Data.rank, new LinkedList<S>());

			fronts.get(r2Data.rank).add(p);			
		}

		this.rankedSubpopulations = new ArrayList<>(fronts.size());
		Iterator<Integer> iterator = fronts.keySet().iterator();
		while (iterator.hasNext())
			this.rankedSubpopulations.add(fronts.get(iterator.next()));

		return this;
	}

	@Override
	public List<S> getSubfront(int rank) {
		return this.rankedSubpopulations.get(rank);
	}

	@Override
	public int getNumberOfSubfronts() {
		return this.rankedSubpopulations.size();
	}
		

}
