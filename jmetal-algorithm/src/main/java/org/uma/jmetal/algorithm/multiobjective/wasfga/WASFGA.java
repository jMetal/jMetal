package org.uma.jmetal.algorithm.multiobjective.wasfga;

import org.uma.jmetal.algorithm.InteractiveAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.mombi.AbstractMOMBI;
import org.uma.jmetal.algorithm.multiobjective.mombi.util.ASFWASFGA;
import org.uma.jmetal.algorithm.multiobjective.mombi.util.AbstractUtilityFunctionsSet;
import org.uma.jmetal.algorithm.multiobjective.mombi.util.Normalizer;
import org.uma.jmetal.algorithm.multiobjective.wasfga.util.WASFGARankingConstraint;
import org.uma.jmetal.algorithm.multiobjective.wasfga.util.WeightVector;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the preference based algorithm named WASF-GA on jMetal5.0
 *
 * @author Juanjo Durillo
 *
 *         This algorithm is described in the paper: A.B. Ruiz, R. Saborido, M.
 *         Luque "A Preference-based Evolutionary Algorithm for Multiobjective
 *         Optimization: The Weighting Achievement Scalarizing Function Genetic
 *         Algorithm". Journal of Global Optimization. May 2015, Volume 62,
 *         Issue 1, pp 101-129
 *         DOI = {10.1007/s10898-014-0214-y}
 */
public class WASFGA<S extends Solution<?>> extends AbstractMOMBI<S> implements
		InteractiveAlgorithm<S,List<S>> {
	private static final long serialVersionUID = 1L;
	protected int maxEvaluations;
	protected int evaluations;
	protected Normalizer normalizer;
	protected double epsilon ;
	protected double[][] weights;
	
	private final AbstractUtilityFunctionsSet<S> achievementScalarizingFunction;
	private List<Double> interestPoint = null;
	private String weightVectorsFileName = "" ;

	/**
	 * Constructor
	 *
	 * @param problem Problem to solve
	 */
	public WASFGA(Problem<S> problem,
								int populationSize,
								int maxIterations,
								CrossoverOperator<S> crossoverOperator,
								MutationOperator<S> mutationOperator,
								SelectionOperator<List<S>, S> selectionOperator,
								SolutionListEvaluator<S> evaluator,
                double epsilon,
								List<Double> referencePoint,
								String weightVectorsFileName) {

		super(problem,maxIterations,crossoverOperator,mutationOperator,selectionOperator,evaluator);
		this.weightVectorsFileName = weightVectorsFileName ;
		setMaxPopulationSize(populationSize);
		this.interestPoint = referencePoint;
		this.achievementScalarizingFunction =  createUtilityFunction();
		this.epsilon = epsilon ;
	}
	
	/**
	 * Constructor
	 *
	 * @param problem Problem to solve
	 */
	public WASFGA(Problem<S> problem,
								int populationSize,
								int maxIterations,
								CrossoverOperator<S> crossoverOperator,
								MutationOperator<S> mutationOperator,
								SelectionOperator<List<S>, S> selectionOperator,
								SolutionListEvaluator<S> evaluator,
                double epsilon,
                List<Double> referencePoint) {
		
		this(problem,
						populationSize,
						maxIterations,
						crossoverOperator,
						mutationOperator,
						selectionOperator,
						evaluator,
            epsilon,
						referencePoint,
						"") ;
	}

	public AbstractUtilityFunctionsSet<S> createUtilityFunction() {
		WeightVector weightVector = new WeightVector() ;

		//If a file with weight vectors is not given as parameter, weights are calculated or read from the resources file of jMetal
		if ("".equals(this.weightVectorsFileName)) {
			//For two biobjective problems weights are computed
			if (problem.getNumberOfObjectives() == 2) {
				weights = weightVector.initUniformWeights2D(epsilon, getMaxPopulationSize());
			}
			//For more than two objectives, weights are read from the resources file of jMetal
			else {
				String dataFileName = "W" + problem.getNumberOfObjectives() + "D_" + getMaxPopulationSize() + ".dat";
				weights = weightVector.getWeightsFromResourcesInJMetal("MOEAD_Weights/" + dataFileName);
			}
		} else { //If a file with weight vectors is given as parameter, weights are read from that file
			weights = weightVector.getWeightsFromFile(this.weightVectorsFileName) ;
		}
		weights = WeightVector.invertWeights(weights,true);
		
		if (weights.length != maxPopulationSize) {
			throw new JMetalException("The number of weight vectors (" + weights.length +") and the population size(" +
							maxPopulationSize + ") have different values") ;
		}
		
		ASFWASFGA<S> aux = new ASFWASFGA<>(weights, interestPoint);

		return aux;
	}

	public void updatePointOfInterest(List<Double> newPointOfInterest) {
		((ASFWASFGA<S>)this.achievementScalarizingFunction).updatePointOfInterest(newPointOfInterest);
	}

	public int getPopulationSize() {
		return getMaxPopulationSize();
	}

	@Override
	public void specificMOEAComputations() {
		updateNadirPoint(this.getPopulation());
		updateReferencePoint(this.getPopulation());
	}

	@Override
	protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
		List<S> jointPopulation = new ArrayList<>();
		jointPopulation.addAll(population);
		jointPopulation.addAll(offspringPopulation);
		Ranking<S> ranking = computeRanking(jointPopulation);
		return selectBest(ranking);
	}
	
	protected Ranking<S> computeRanking(List<S> solutionList) {
		Ranking<S> ranking = new WASFGARankingConstraint<>(this.achievementScalarizingFunction);
		ranking.computeRanking(solutionList);
		return ranking;
	}
	
	protected void addRankedSolutionsToPopulation(Ranking<S> ranking, int index, List<S> population) {
		population.addAll(ranking.getSubfront(index));
	}
	
	protected void addLastRankedSolutionsToPopulation(Ranking<S> ranking,int index, List<S>population) {
		List<S> front 	= ranking.getSubfront(index);
		int remain 		= this.getPopulationSize() - population.size();
		population.addAll(front.subList(0, remain));
	}
	
	protected List<S> selectBest(Ranking<S> ranking) {
		List<S> population = new ArrayList<>(this.getPopulationSize());
		int rankingIndex = 0;

		while (populationIsNotFull(population)) {
			if (subfrontFillsIntoThePopulation(ranking, rankingIndex, population)) {
				addRankedSolutionsToPopulation(ranking, rankingIndex, population);
				rankingIndex++;
			} else {
				addLastRankedSolutionsToPopulation(ranking, rankingIndex, population);
			}
		}
		return population;
	}

	private boolean subfrontFillsIntoThePopulation(Ranking<S> ranking, int index, List<S> population) {
		return (population.size()+ranking.getSubfront(index).size() < this.getPopulationSize());
	}
	protected AbstractUtilityFunctionsSet<S> getUtilityFunctions() {
		return this.achievementScalarizingFunction;
	}
	
	@Override public List<S> getResult() {
		return getNonDominatedSolutions(getPopulation());
	}
	protected List<S> getNonDominatedSolutions(List<S> solutionList) {
		return SolutionListUtils.getNondominatedSolutions(solutionList);
	}

	@Override public String getName() {
		return "WASFGA" ;
	}

	@Override public String getDescription() {
		return "Weighting Achievement Scalarizing Function Genetic Algorithm" ;
	}



}
