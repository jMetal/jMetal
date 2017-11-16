package org.uma.jmetal.algorithm.multiobjective.wasfga.util;

import org.uma.jmetal.algorithm.multiobjective.mombi.util.AbstractUtilityFunctionsSet;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("serial")
public class WASFGARankingConstraint<S extends Solution<?>> extends GenericSolutionAttribute<S, Integer>
		implements Ranking<S> {

  private List<List<S>> rankedSubpopulations;
  private int numberOfRanks = 0;
  //private AbstractUtilityFunctionsSet<S> utilityFunctionsUtopia;
  private AbstractUtilityFunctionsSet<S> utilityFunctions;
  private NumberOfViolatedConstraints<S> numberOfViolatedConstraints ;
  private OverallConstraintViolation<S> constraintViolation;

  public WASFGARankingConstraint( AbstractUtilityFunctionsSet<S> utilityFunctionsNadir) {
    this.utilityFunctions = utilityFunctionsNadir;
    //this.utilityFunctionsUtopia = utilityFunctionsUtopia;
    this.numberOfViolatedConstraints = new NumberOfViolatedConstraints<S>() ;
	this.constraintViolation = new OverallConstraintViolation<>();
  }

	@Override
	public Ranking<S> computeRanking(List<S> population) {


		List<S> temporalList 	= new LinkedList<>();
		//temporalList.addAll(population);

		List<S> feasibleList = new ArrayList<>();
		List<S> unfeasibleList = new ArrayList<>();

		int numberOfFeasibleFrontiersInNewPopulation, numberOfUnfeasibleFrontiersInNewPopulation;

		for (S solution:population) {

			if((numberOfViolatedConstraints.getAttribute(solution)!= null &&
					numberOfViolatedConstraints.getAttribute(solution)>0)){//|| (constraintViolation.getAttribute(solution)!=null && constraintViolation.getAttribute(solution)==-93)
				unfeasibleList.add(solution);
			}else {
				feasibleList.add(solution);
			}
		}
		temporalList.addAll(feasibleList);
		temporalList.addAll(unfeasibleList);
		int numberOfWeights = this.utilityFunctions.getSize() ;
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


		for (int idx = 0; idx < this.numberOfRanks; idx++) {
			if(feasibleList!=null && !feasibleList.isEmpty()) {
				for (int weigth = 0; weigth < this.utilityFunctions.getSize(); weigth++) {
					if(feasibleList!=null && !feasibleList.isEmpty()) {
						int toRemoveIdx = 0;
						double minimumValue = this.utilityFunctions.evaluate(feasibleList.get(0), weigth);
						for (int solutionIdx = 1; solutionIdx < feasibleList.size(); solutionIdx++) {
							double value = this.utilityFunctions.evaluate(feasibleList.get(solutionIdx), weigth);

							if (value < minimumValue) {
								minimumValue = value;
								toRemoveIdx = solutionIdx;
							}
						}

						S solutionToInsert = feasibleList.remove(toRemoveIdx);
						setAttribute(solutionToInsert, idx);
						this.rankedSubpopulations.get(idx).add(solutionToInsert);
					}
				}
			}else if(unfeasibleList!=null && !unfeasibleList.isEmpty()){
				for (int weigth = 0; weigth < this.utilityFunctions.getSize(); weigth++) {
					if(unfeasibleList!=null && !unfeasibleList.isEmpty()) {
						int toRemoveIdx = 0;
						double minimumValue = this.utilityFunctions.evaluate(unfeasibleList.get(0), weigth);
						for (int solutionIdx = 1; solutionIdx < unfeasibleList.size(); solutionIdx++) {
							double value = this.utilityFunctions.evaluate(unfeasibleList.get(solutionIdx), weigth);

							if (value < minimumValue) {
								minimumValue = value;
								toRemoveIdx = solutionIdx;
							}
						}

						S solutionToInsert = unfeasibleList.remove(toRemoveIdx);
						setAttribute(solutionToInsert, idx);
						this.rankedSubpopulations.get(idx).add(solutionToInsert);
					}
				}
			}
		}
		
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
