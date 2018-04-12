package org.uma.jmetal.algorithm.multiobjective.wasfga.util;

import org.uma.jmetal.algorithm.multiobjective.mombi.util.AbstractUtilityFunctionsSet;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Rubén Saborido
 *
 * Implementation of the ranking procedure for the preference based algorithm named WASF-GA on jMetal5.0
 *
 * It classifies solutions into different fronts.
 *
 *
 * If the problem contains constraints, after feasible solutions it classifies the unfeasible solutions into fronts:
 *
 * - Each unfeasible solutions goes into a different front.
 * - Unfeasible solutions with lower number of violated constraints are preferred.
 * - If two solutions have equal number of violated constraints it compares the overall constraint values.
 * - If two solutions have equal overall constraint values it compares de values of the utility function.
 *
 */
public class WASFGARanking<S extends Solution<?>> extends GenericSolutionAttribute<S, Integer>
		implements Ranking<S> {

	private AbstractUtilityFunctionsSet<S> utilityFunctions;
	private List<List<S>> rankedSubpopulations;
	private int numberOfRanks;
	private NumberOfViolatedConstraints<S> numberOfViolatedConstraints ;
	private OverallConstraintViolation<S> overallConstraintViolation;

	public WASFGARanking(AbstractUtilityFunctionsSet<S> utilityFunctions) {
		this.numberOfRanks = 0;
		this.utilityFunctions = utilityFunctions;
		this.numberOfViolatedConstraints = new NumberOfViolatedConstraints<S>() ;
		this.overallConstraintViolation = new OverallConstraintViolation<S>();
	}

	@Override
	public Ranking<S> computeRanking(List<S> population) {
		int numberOfRanksForFeasibleSolutions, numberOfRanksForUnfeasibleSolutions, rank, indexOfBestSolution;
		int index, indexOfWeight;
		int numberOfWeights = this.utilityFunctions.getSize();
		int[] rankForUnfeasibleSolutions;
		double value, minimumValue;
		List<S> feasibleSolutions = new LinkedList<>();
		List<S> unfeasibleSolutions = new LinkedList<>();
		S solutionToInsert;

		//Split the population in feasible and unfeasible solutions
		for (S solution:population) {
			if((numberOfViolatedConstraints.getAttribute(solution)!= null
					&&
					numberOfViolatedConstraints.getAttribute(solution)>0)) {
				unfeasibleSolutions.add(solution);
			}
			else {
				feasibleSolutions.add(solution);
			}
		}

		//Compute the number of fronts for feasible solutions
		if(feasibleSolutions.size() > 0){
			if(feasibleSolutions.size() > numberOfWeights){
				numberOfRanksForFeasibleSolutions = (feasibleSolutions.size() + 1) / numberOfWeights;
			}
			else{
				numberOfRanksForFeasibleSolutions = 1;
			}
		}
		else {
			numberOfRanksForFeasibleSolutions = 0;
		}

		//Each unfeasible solution goes to a different front
		numberOfRanksForUnfeasibleSolutions = unfeasibleSolutions.size();

		//Initialization of properties
		this.numberOfRanks = numberOfRanksForFeasibleSolutions + numberOfRanksForUnfeasibleSolutions;
		this.rankedSubpopulations = new ArrayList<>(this.numberOfRanks);
		for (int i = 0; i < this.numberOfRanks; i++) {
			this.rankedSubpopulations.add(new ArrayList<S>());
		}

		//Classification of feasible solutions
		if (!feasibleSolutions.isEmpty()) {
			//Iteration for each front
			for (index = 0; index < numberOfRanksForFeasibleSolutions; index++) {
				//Iteration for each weight vector
				for (indexOfWeight = 0; indexOfWeight < numberOfWeights; indexOfWeight++) {
					if (!feasibleSolutions.isEmpty()) {
						indexOfBestSolution = 0;
						minimumValue = this.utilityFunctions.evaluate(feasibleSolutions.get(0), indexOfWeight);
						for (int solutionIdx = 1; solutionIdx < feasibleSolutions.size(); solutionIdx++) {
							value = this.utilityFunctions.evaluate(feasibleSolutions.get(solutionIdx), indexOfWeight);

							if (value < minimumValue) {
								minimumValue = value;
								indexOfBestSolution = solutionIdx;
							}
						}

						//Introduce the best feasible individual for the current weight vector into the current front
						solutionToInsert = feasibleSolutions.remove(indexOfBestSolution);
						setAttribute(solutionToInsert, index);
						this.rankedSubpopulations.get(index).add(solutionToInsert);
					}
				}
			}
		}

		//Classification of unfeasible solutions
		if (!unfeasibleSolutions.isEmpty()) {
			//Obtain the rank of each unfeasible solution
			rankForUnfeasibleSolutions = rankUnfeasibleSolutions(unfeasibleSolutions);

			//Add each unfeasible solution into their corresponding front
			for (index = 0; index < rankForUnfeasibleSolutions.length; index++) {
				solutionToInsert = unfeasibleSolutions.get(index);
				rank = rankForUnfeasibleSolutions[index] + numberOfRanksForFeasibleSolutions;
				setAttribute(solutionToInsert, rank);
				this.rankedSubpopulations.get(rank).add(solutionToInsert);
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

	public AbstractUtilityFunctionsSet<S> getUtilityFunctions() {
		return this.utilityFunctions;
	}

	/**
	 * Obtain the rank of each solution in a list of unfeasible solutions
	 *
	 * @param population List of unfeasible solutions
	 * @return The rank of each unfeasible solutions
	 */
	protected int[] rankUnfeasibleSolutions(List<S> population){
		int numberOfViolatedConstraintsBySolution1, numberOfViolatedConstraintsBySolution2;
		int indexOfFirstSolution, indexOfSecondSolution, indexOfWeight;
		double overallConstraintViolationSolution1, overallConstraintViolationSolution2;
		double minimumValueFirstSolution, minimumValueSecondSolution, value;
		int[] rank = new int[population.size()];
		Arrays.fill(rank, 0);

		//Iteration for each solution
		for (indexOfFirstSolution = 0; indexOfFirstSolution < population.size()-1; indexOfFirstSolution++ ) {
			//The current solution is compared with the following ones
			for (indexOfSecondSolution = indexOfFirstSolution + 1; indexOfSecondSolution < population.size(); indexOfSecondSolution++) {
				numberOfViolatedConstraintsBySolution1 = numberOfViolatedConstraints.getAttribute(population.get(indexOfFirstSolution));
				numberOfViolatedConstraintsBySolution2 = numberOfViolatedConstraints.getAttribute(population.get(indexOfSecondSolution));

				//The number of violated constraints is compared.
				//A solution with higher number of violated constraints has a worse (higher) rank
				if (numberOfViolatedConstraintsBySolution1 > numberOfViolatedConstraintsBySolution2) {
					rank[indexOfFirstSolution]++;
				} else if (numberOfViolatedConstraintsBySolution1 < numberOfViolatedConstraintsBySolution2) {
					rank[indexOfSecondSolution]++;
				} else {
					//Because the solutions have a similar violated number of constraints, the overall constraint
					//violation values are compared.
					//Note that overall constraint values are negative in jMetal
					overallConstraintViolationSolution1 = overallConstraintViolation.getAttribute(population.get(indexOfFirstSolution));
					overallConstraintViolationSolution2 = overallConstraintViolation.getAttribute(population.get(indexOfSecondSolution));

					//The overall constraint violation values are compared.
					//Note that overall constraint values are negative in jMetal.
					//Thus, a solution with higher value has a better (higher) rank
					if (overallConstraintViolationSolution1 > overallConstraintViolationSolution2) {
						rank[indexOfSecondSolution]++;
					} else if (overallConstraintViolationSolution1 < overallConstraintViolationSolution2) {
						rank[indexOfFirstSolution]++;
					} else {
						//Because the solutions have the same overall constraint violation values, we compare the
						//the value of their utility functions. Lower values are better.
						minimumValueFirstSolution = minimumValueSecondSolution = Double.POSITIVE_INFINITY;
						for (indexOfWeight = 0; indexOfWeight < this.utilityFunctions.getSize(); indexOfWeight++) {
							value = this.utilityFunctions.evaluate(population.get(indexOfFirstSolution), indexOfWeight);
							if (value < minimumValueFirstSolution) {
								minimumValueFirstSolution = value;
							}

							value = this.utilityFunctions.evaluate(population.get(indexOfSecondSolution), indexOfWeight);
							if (value < minimumValueSecondSolution) {
								minimumValueSecondSolution = value;
							}
						}

						if (minimumValueFirstSolution < minimumValueSecondSolution) {
							rank[indexOfSecondSolution]++;
						} else {
							rank[indexOfFirstSolution]++;
						}
					}
				}
			}
		}

		return rank;
	}

}