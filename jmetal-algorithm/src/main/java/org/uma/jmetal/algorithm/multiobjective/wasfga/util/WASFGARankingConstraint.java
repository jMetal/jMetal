package org.uma.jmetal.algorithm.multiobjective.wasfga.util;

import org.uma.jmetal.algorithm.multiobjective.mombi.util.AbstractUtilityFunctionsSet;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class WASFGARankingConstraint<S extends Solution<?>> extends GenericSolutionAttribute<S, Integer>
		implements Ranking<S> {

  private List<List<S>> rankedSubpopulations;
  private int numberOfRanks = 0;
  private AbstractUtilityFunctionsSet<S> utilityFunctionsUtopia;
  private AbstractUtilityFunctionsSet<S> utilityFunctionsNadir;
  private NumberOfViolatedConstraints<S> numberOfViolatedConstraints ;


  public WASFGARankingConstraint(AbstractUtilityFunctionsSet<S> utilityFunctionsUtopia, AbstractUtilityFunctionsSet<S> utilityFunctionsNadir) {
    this.utilityFunctionsNadir = utilityFunctionsNadir;
    this.utilityFunctionsUtopia = utilityFunctionsUtopia;
    this.numberOfViolatedConstraints = new NumberOfViolatedConstraints<S>() ;

  }

	@Override
	public Ranking<S> computeRanking(List<S> population) {


		List<S> temporalList 	= new LinkedList<>();
		//temporalList.addAll(population);

		List<S> feasibleList = new ArrayList<>();
		List<S> unfeasibleList = new ArrayList<>();

		int numberOfFeasibleFrontiersInNewPopulation, numberOfUnfeasibleFrontiersInNewPopulation;

		for (S solution:population) {
			if(numberOfViolatedConstraints.getAttribute(solution)>0){
				unfeasibleList.add(solution);
			}else{
				feasibleList.add(solution);
			}
		}
		temporalList.addAll(feasibleList);
		temporalList.addAll(unfeasibleList);
		int numberOfWeights = this.utilityFunctionsUtopia.getSize() ;
		if(feasibleList.size() > 0){
			if(feasibleList.size() > numberOfWeights){
				numberOfFeasibleFrontiersInNewPopulation = 2;
			}else{
				numberOfFeasibleFrontiersInNewPopulation = 1;
			}
		}else {
			numberOfFeasibleFrontiersInNewPopulation = 0;
		}
		if (unfeasibleList.size() > 0) {
			if (unfeasibleList.size() > numberOfWeights) {
				numberOfUnfeasibleFrontiersInNewPopulation = 2;
			}
			else
				numberOfUnfeasibleFrontiersInNewPopulation = 1;
		}
		else
			numberOfUnfeasibleFrontiersInNewPopulation = 0;

		this.numberOfRanks = numberOfFeasibleFrontiersInNewPopulation + numberOfUnfeasibleFrontiersInNewPopulation;
		//(population.size() + 1) / (this.utilityFunctionsUtopia.getSize() + this.utilityFunctionsNadir.getSize());

		this.rankedSubpopulations = new ArrayList<>(this.numberOfRanks);

		for (int i = 0; i < this.numberOfRanks; i++) {
			this.rankedSubpopulations.add(new ArrayList<S>());
		}


		if(feasibleList!=null && !feasibleList.isEmpty()){
			for(int idx = 0 ;idx < numberOfFeasibleFrontiersInNewPopulation;idx++){
				for (int weigth = 0; weigth < this.utilityFunctionsUtopia.getSize(); weigth++) {
					int toRemoveIdx = 0;
					if(!feasibleList.isEmpty()) {
						double minimumValue = this.utilityFunctionsUtopia.evaluate(feasibleList.get(0), weigth);
						for (int solutionIdx = 1; solutionIdx < feasibleList.size(); solutionIdx++) {
							double value = this.utilityFunctionsUtopia.evaluate(temporalList.get(solutionIdx), weigth);

							if (value < minimumValue) {
								minimumValue = value;
								toRemoveIdx = solutionIdx;
							}
						}

						S solutionToInsert = feasibleList.remove(toRemoveIdx);
						setAttribute(solutionToInsert, idx);
						temporalList.remove(solutionToInsert);
						this.rankedSubpopulations.get(idx).add(solutionToInsert);
					}
				}

				for (int weigth = 0; weigth < this.utilityFunctionsNadir.getSize(); weigth++) {
					int toRemoveIdx = 0;
					if(!feasibleList.isEmpty()) {
						double minimumValue = this.utilityFunctionsNadir.evaluate(feasibleList.get(0), weigth);
						for (int solutionIdx = 1; solutionIdx < feasibleList.size(); solutionIdx++) {
							double value = this.utilityFunctionsNadir.evaluate(feasibleList.get(solutionIdx), weigth);

							if (value < minimumValue) {
								minimumValue = value;
								toRemoveIdx = solutionIdx;
							}
						}

						S solutionToInsert = feasibleList.remove(toRemoveIdx);
						setAttribute(solutionToInsert, idx);
						temporalList.remove(solutionToInsert);
						this.rankedSubpopulations.get(idx).add(solutionToInsert);
					}
				}
			}
		}
		if(unfeasibleList!=null && !unfeasibleList.isEmpty()){
			for(int idx = numberOfFeasibleFrontiersInNewPopulation ;idx < numberOfFeasibleFrontiersInNewPopulation + numberOfUnfeasibleFrontiersInNewPopulation;idx++){
				for (int weigth = 0; weigth < this.utilityFunctionsUtopia.getSize(); weigth++) {
					int toRemoveIdx = 0;
					if(!unfeasibleList.isEmpty()) {
						double minimumValue = this.utilityFunctionsUtopia.evaluate(unfeasibleList.get(0), weigth);
						for (int solutionIdx = 1; solutionIdx < unfeasibleList.size(); solutionIdx++) {
							double value = this.utilityFunctionsUtopia.evaluate(unfeasibleList.get(solutionIdx), weigth);

							if (value < minimumValue) {
								minimumValue = value;
								toRemoveIdx = solutionIdx;
							}
						}

						S solutionToInsert = unfeasibleList.remove(toRemoveIdx);
						setAttribute(solutionToInsert, idx);
						temporalList.remove(solutionToInsert);
						this.rankedSubpopulations.get(idx).add(solutionToInsert);
					}
				}

				for (int weigth = 0; weigth < this.utilityFunctionsNadir.getSize(); weigth++) {
					int toRemoveIdx = 0;
					if(!unfeasibleList.isEmpty()) {
						double minimumValue = this.utilityFunctionsNadir.evaluate(unfeasibleList.get(0), weigth);
						for (int solutionIdx = 1; solutionIdx < unfeasibleList.size(); solutionIdx++) {
							double value = this.utilityFunctionsNadir.evaluate(unfeasibleList.get(solutionIdx), weigth);

							if (value < minimumValue) {
								minimumValue = value;
								toRemoveIdx = solutionIdx;
							}
						}

						S solutionToInsert = unfeasibleList.remove(toRemoveIdx);
						setAttribute(solutionToInsert, idx);
						temporalList.remove(solutionToInsert);
						this.rankedSubpopulations.get(idx).add(solutionToInsert);
					}
				}
			}
		}
		/*for (int idx = 0; idx < this.numberOfRanks; idx++) {
			for (int weigth = 0; weigth < this.utilityFunctionsUtopia.getSize(); weigth++) {
				int toRemoveIdx = 0;
				double minimumValue = this.utilityFunctionsUtopia.evaluate(temporalList.get(0), weigth);
				for (int solutionIdx = 1; solutionIdx < temporalList.size(); solutionIdx++) {
					double value = this.utilityFunctionsUtopia.evaluate(temporalList.get(solutionIdx), weigth);

					if (value < minimumValue) {
						minimumValue = value;
						toRemoveIdx = solutionIdx;
					}
				}

				S solutionToInsert = temporalList.remove(toRemoveIdx);
				setAttribute(solutionToInsert, idx);
				this.rankedSubpopulations.get(idx).add(solutionToInsert);
			}
			for (int weigth = 0; weigth < this.utilityFunctionsNadir.getSize(); weigth++) {
				int toRemoveIdx = 0;
				double minimumValue = this.utilityFunctionsNadir.evaluate(temporalList.get(0), weigth);
				for (int solutionIdx = 1; solutionIdx < temporalList.size(); solutionIdx++) {
					double value = this.utilityFunctionsNadir.evaluate(temporalList.get(solutionIdx), weigth);

					if (value < minimumValue) {
						minimumValue = value;
						toRemoveIdx = solutionIdx;
					}
				}

				S solutionToInsert = temporalList.remove(toRemoveIdx);
				setAttribute(solutionToInsert, idx);
				this.rankedSubpopulations.get(idx).add(solutionToInsert);
			}

		}*/



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
