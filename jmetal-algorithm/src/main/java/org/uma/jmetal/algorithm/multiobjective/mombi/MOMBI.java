package org.uma.jmetal.algorithm.multiobjective.mombi;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;

public class MOMBI<S extends Solution<?>> extends AbstractMOMBI<S>{
	
	
	AbstractUtilityFunctionsSet<S> utilityFunctions;
	
	public MOMBI(Problem<S> problem, 
			int maxIterations, 
			int populationSize, 
			CrossoverOperator<S> crossover,
			MutationOperator<S> mutation, 
			SelectionOperator<List<S>, S> selection, 
			SolutionListEvaluator<S> evaluator,
			String pathWeights) {
		super(problem, maxIterations, populationSize, crossover, mutation, selection, evaluator);
		this.utilityFunctions = new TchebycheffUtilityFunctionsSet(pathWeights, this.getReferencePoint());
	}
	
	
	
	@Override
	public void specificMOEAComputations() {
		updateReferencePoint(this.getPopulation());
		updateNadirPoint(this.getPopulation());								
	}

	@Override
	protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
		List<S> jointPopulation = new ArrayList<>();
		jointPopulation.addAll(population);
		jointPopulation.addAll(offspringPopulation);
		
		Ranking<S> ranking = computeRanking(jointPopulation);
		return selectBests(ranking);
		
		return null;
	}

	
	protected Ranking<S> computeRanking(List<S> solutionList) {
		Ranking<S> ranking = new R2Ranking<>(this.utilityFunctions);
		ranking.computeRanking(solutionList);
		
		return ranking;
	}
	
	protected List<S> crowdingDistanceSelection(Ranking<S> ranking) {
	    CrowdingDistance<S> crowdingDistance = new CrowdingDistance<S>();
	    List<S> population = new ArrayList<>(populationSize);
	    int rankingIndex = 0;
	    while (populationIsNotFull(population)) {
	      if (subfrontFillsIntoThePopulation(ranking, rankingIndex, population)) {
	        addRankedSolutionsToPopulation(ranking, rankingIndex, population);
	        rankingIndex++;
	      } else {
	        crowdingDistance.computeDensityEstimator(ranking.getSubfront(rankingIndex));
	        addLastRankedSolutionsToPopulation(ranking, rankingIndex, population);
	      }
	    }

	    return population;
	}
	
	
	
}
