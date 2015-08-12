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

public class R2Ranking<S extends Solution<?>> extends GenericSolutionAttribute<S, Integer>implements Ranking<S> {

	private AbstractUtilityFunctionsSet utilityFunctions;
	private List<List<S>> rankedSubpopulations;
	private int numberOfRanks = 0;

	public R2Ranking(AbstractUtilityFunctionsSet utilityFunctions) {
		this.utilityFunctions = utilityFunctions;
	}

	@Override
	public Ranking<S> computeRanking(List<S> population) {

		for (S solution : population)
			solution.setAttribute(getAttributeID(), new R2SolutionData());

		for (int i = 0; i < this.utilityFunctions.getSize(); i++) {
			for (S p : population) {
				((R2SolutionData) p.getAttribute(getAttributeID())).alpha = this.utilityFunctions.evaluate(p, i);

				if (((R2SolutionData) p.getAttribute(getAttributeID())).alpha < ((R2SolutionData) p
						.getAttribute(getAttributeID())).utility)
					((R2SolutionData) p.getAttribute(getAttributeID())).utility = ((R2SolutionData) p
							.getAttribute(getAttributeID())).alpha;

			}

			Collections.sort(population, new Comparator<R2Ranking<S>>() {
				@Override
				public int compare(R2Ranking<S> o1, R2Ranking<S> o2) {
					R2SolutionData data1 = (R2SolutionData) o1.getAttribute(o1.getClass());
					R2SolutionData data2 = (R2SolutionData) o2.getAttribute(Ranking);
					if (data1.alpha < data2.alpha)
						return -1;
					else if (data1.alpha > data2.alpha)
						return 1;
					else
						return 0;
				}
			});

			int rank = 1;
			for (Solution p : population) {
				if (rank < p.getRight().rank) {
					p.getLeft().setAttribute(getAttributeID(), rank);
					numberOfRanks = Math.max(numberOfRanks, rank);
				}
				rank = rank + 1;
			}
		}

		Map<Integer, List<S>> fronts = new TreeMap<>(); // sorted on key
		for (Pair<S, R2SolutionData> p : population) {
			if (fronts.get(p.getRight().rank) == null)
				fronts.put(p.getRight().rank, new LinkedList<S>());

			fronts.get(p.getRight().rank).add(p.getLeft());
			p.getLeft().setAttribute(getAttributeID(), p.getRight().rank);
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
