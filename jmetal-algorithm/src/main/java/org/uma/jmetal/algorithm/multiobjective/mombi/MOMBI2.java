package org.uma.jmetal.algorithm.multiobjective.mombi;

import org.uma.jmetal.algorithm.multiobjective.mombi.util.*;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Juan J. Durillo
 * @version 1.0
 * This class implements the MOMBI2 algorithm (a.k.a. MOMBI-II)
 *
 * Reference: Improved Metaheuristic Based on the R2 Indicator for Many-Objective Optimization.
 * R. Hernández Gómez, C.A. Coello Coello. Proceeding GECCO '15 Proceedings of the 2015 on Genetic
 * and Evolutionary Computation Conference. Pages 679-686
 * DOI: 10.1145/2739480.2754776
 */
@SuppressWarnings("serial") // remove warning for serialization
public class MOMBI2<S extends Solution<?>> extends MOMBI<S> {
	
	protected final MOMBI2History<S> history;
	protected final Double alpha		= 0.5;
	protected final Double epsilon 		= 1.0e-3;
	protected List<Double> maxs;
	protected Normalizer normalizer;

	/**
	 * Creates a new instance of the MOMBI algorithm
	 * @param problem
	 * @param maxIterations
	 * @param crossover
	 * @param mutation
	 * @param selection
	 * @param evaluator
	 * @param pathWeights
	 */
	public MOMBI2(Problem<S> problem, int maxIterations, CrossoverOperator<S> crossover, MutationOperator<S> mutation,
			SelectionOperator<List<S>, S> selection, SolutionListEvaluator<S> evaluator, String pathWeights) {
		super(problem, maxIterations, crossover, mutation, selection, evaluator, pathWeights);

		this.history = new MOMBI2History<>(problem.getNumberOfObjectives());
	}
	
	protected void updateMax(List<S> population) {

		for (S solution : population)
			for (int i = 0; i < this.maxs.size(); i++)
				this.maxs.set(i,Math.max(this.maxs.get(i),solution.getObjective(i)));
			
		this.history.add(maxs);
	}

	@Override
	protected void initProgress() {
		super.initProgress();
		this.updateMax(this.getPopulation());
	}

	@Override
	public AbstractUtilityFunctionsSet<S> createUtilityFunction(String pathWeights) {
		System.out.println("MOMBI 2");
		this.maxs    = new ArrayList<>(Collections.nCopies(getProblem().getNumberOfObjectives(),
															Double.NEGATIVE_INFINITY));
		this.normalizer = new Normalizer(this.getReferencePoint(), maxs);
		ASFUtilityFunctionSet<S> aux = new ASFUtilityFunctionSet<>(pathWeights);
		aux.setNormalizer(this.normalizer);
		return aux;
	}
	
	
	// ToDo: refactor this method (first implementation just try to mimic c implementation)
	@Override
	public void updateReferencePoint(List<S> population) {
		List<Double> iterationMaxs = new ArrayList<>(maxs.size());
		
		for (int i = 0; i < this.getProblem().getNumberOfObjectives(); i++) {		
			iterationMaxs.add(Double.NEGATIVE_INFINITY);
		}
		
		for (S solution : population) {
			updateReferencePoint(solution);
			for (int i = 0; i < solution.getNumberOfObjectives(); i++) {				
				iterationMaxs.set(i, Math.max(iterationMaxs.get(i), solution.getObjective(i)));				
			}
		}
		
		history.add(iterationMaxs);
		
		List<Double> mean = history.mean();
		List<Double> var  = history.variance(mean);
			
		Double maxVariance = this.getMax(var);						
			
		if (maxVariance > alpha) {
			Double maxInNadir = this.getMax(this.maxs);
			for (int i = 0; i < this.getProblem().getNumberOfObjectives(); i++) 
				this.maxs.set(i, maxInNadir);						
		} else {						
			for (int i = 0; i < this.getProblem().getNumberOfObjectives(); i++) {
				if (Math.abs(maxs.get(i) - this.getReferencePoint().get(i)) < this.epsilon) {
					Double maxInMaxs = this.getMax(this.maxs);
					this.maxs.set(i,maxInMaxs);
					history.mark(i);
				} else if (iterationMaxs.get(i) > this.maxs.get(i)) {
					this.maxs.set(i, iterationMaxs.get(i) + Math.abs(iterationMaxs.get(i)-this.maxs.get(i)));
					history.mark(i);
				} else if ((var.get(i)==0.0) && history.isUnMarked(i)) {
					double v = history.getMaxObjective(i);
					this.maxs.set(i, (maxs.get(i)+v)/2.0);
					history.mark(i);
				}
				history.decreaseMark(i);
			}						
		}
	}
	
	protected R2Ranking<S> computeRanking(List<S> solutionList) {
		R2Ranking<S> ranking = new R2RankingNormalized<>(this.getUtilityFunctions(),this.normalizer);
		ranking.computeRanking(solutionList);
		
		return ranking;
	}
	
	public Double getMax(List<Double> list) {
		Double result = Double.NEGATIVE_INFINITY;
		for (Double d : list) 
			result = Math.max(result, d);
		
		return result;
	}

	@Override public String getName() {
		return "MOMBI" ;
	}

	@Override public String getDescription() {
		return "Many-Objective Metaheuristic Based on the R2 Indicator, version 2" ;
	}

}
