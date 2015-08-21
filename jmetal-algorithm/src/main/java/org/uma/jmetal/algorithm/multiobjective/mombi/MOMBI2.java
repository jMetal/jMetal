package org.uma.jmetal.algorithm.multiobjective.mombi;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 * 
 * @author Juan J. Durillo
 * @version 1.0
 * This class implements the MOMBI2 algorithm (a.k.a. MOMBI-II)
 *
 */
@SuppressWarnings("serial") // remove warning for serialization
public class MOMBI2<S extends Solution<?>> extends MOMBI<S> {
	
	private final MOMBI2History<S> history;
	private final Double		   alpha		= 0.5;
	private final Double 		   epsilon 		= 1.0e-3;	
	private  List<Double>     	   maxs;
	private Normalizer             normalizer;
	
	
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
	
	private void updateMax(List<S> population) {
		if (this.maxs.isEmpty())
			for (int i = 0; i < this.getProblem().getNumberOfObjectives(); i++)
				this.maxs.add(Double.NEGATIVE_INFINITY);
		
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
	
	
	public AbstractUtilityFunctionsSet<S> createUtilityFunction(String pathWeights) {
		System.out.println("MOMBI 2");
		//this.mins    = new ArrayList<>(getProblem().getNumberOfObjectives());		
		this.maxs    = new ArrayList<>(getProblem().getNumberOfObjectives());
		this.normalizer = new Normalizer(this.getReferencePoint(), maxs);
		//this.utilityFunctions = new TchebycheffUtilityFunctionsSet<>(pathWeights,this.getReferencePoint());		
		ASFUtilityFunctionSet<S> aux = new ASFUtilityFunctionSet<>(pathWeights,this.getReferencePoint());
		aux.setNormalizer(this.normalizer);
		return aux;
	}
	
	
	// ToDo: refactor this method (first implementation just try to mimic c implementation)
	@Override
	public void updateReferencePoint(List<S> population) {		
		System.out.println("Updating reference point");
		
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
	
	
	
	private class MOMBI2History<T extends Solution<?>> {
		public static final int MAX_LENGHT 			= 5;
		private 	  final int numberOfObjectives; 
		private       final List<List<Double>> history;
		private 	  final List<Integer>      marks;
		
		public MOMBI2History(int numberOfObjectives) {
			this.numberOfObjectives = numberOfObjectives;
			this.history            = new LinkedList<>();
			this.marks				= new ArrayList<>(this.numberOfObjectives);
			for (int i = 0; i < this.numberOfObjectives;i++) {
				this.marks.add(MAX_LENGHT);
			}
		}
		
		/**
		 * Adds a new vector of maxs values to the history. The method ensures that only the 
		 * newest MAX_LENGTH vectors will be kept in the history
		 * @param maxs
		 */
		public void add(List<Double> maxs) {
			List<Double> aux = new ArrayList<>(this.numberOfObjectives);
			aux.addAll(maxs);
			this.history.add(aux);
			if (history.size() > MAX_LENGHT)
				history.remove(0);
		}
		
		/**
		 * Returns the mean of the values contained in the history
		 */
		public List<Double> mean() {
			List<Double> result = new ArrayList<>(this.numberOfObjectives);
			for (int i = 0; i < this.numberOfObjectives; i++) 
				result.add(0.0);
			
			for (List<Double> historyMember : this.history) 
				for (int i = 0; i < this.numberOfObjectives;i++) 
					result.set(i, result.get(i) + historyMember.get(i));
				
			
			for (int i = 0; i < this.numberOfObjectives; i++) 
				result.set(i, result.get(i)/(double)this.history.size());
			
			return result;
			
		}
		
		/**
		 * Returns the variance of the values contained in the history 
		 */
		public List<Double> variance(List<Double> mean) {
			List<Double> result = new ArrayList<>(this.numberOfObjectives);
			for (int i = 0; i < this.numberOfObjectives; i++) 
				result.add(0.0);
			
			for (List<Double> historyMember : this.history)
				for (int i = 0; i < this.numberOfObjectives; i++) 
					result.set(i, result.get(i) + Math.pow(historyMember.get(i)-mean.get(i), 2.0));
				
			for (int i = 0; i < this.numberOfObjectives; i++)
				result.set(i, result.get(i) / (double)this.history.size());
			
			return result;
			
		}
		
		/**
		 * Return the std of  the values cotained in the history
		 */
		public List<Double> std(List<Double> mean) {
			List<Double> result = new ArrayList<>(mean.size());
			result.addAll(this.variance(mean));
			for (int i = 0; i < result.size(); i++) 
				result.set(i,Math.sqrt(result.get(i)));
			
			return result;
			
		}
		
		public void mark(int index) {
			this.marks.set(index, MAX_LENGHT);
		}
		
		public boolean isUnMarked(int index) {
			return this.marks.get(index) == 0;
		}
		
		public void decreaseMark(int index) {
			if (this.marks.get(index) > 0)
				this.marks.set(index,this.marks.get(index)-1);
		}
		
		public Double getMaxObjective(int index) {
			Double result = Double.NEGATIVE_INFINITY;
			
			for (List<Double> list : this.history)
				result = Math.max(result, list.get(index));
		
			return result;
		}
		
	}
	

}
