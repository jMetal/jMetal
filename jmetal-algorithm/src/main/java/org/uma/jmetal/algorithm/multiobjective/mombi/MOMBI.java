package org.uma.jmetal.algorithm.multiobjective.mombi;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

public class MOMBI<S extends Solution<?>> extends AbstractMOMBI<S>{
	private List<Double> referencePoint  = null;
	private List<Double> nadirPoint      = null;
	
	public MOMBI(Problem<S> problem, 
			int maxIterations, 
			int populationSize, 
			CrossoverOperator<S> crossover,
			MutationOperator<S> mutation, 
			SelectionOperator<List<S>, S> selection, 
			SolutionListEvaluator<S> evaluator) {
		super(problem, maxIterations, populationSize, crossover, mutation, selection, evaluator);
		
	}
	
	private void initializeReferenceVector(int size) {
		this.referencePoint = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
			this.referencePoint.add(Double.POSITIVE_INFINITY);
	}
	
	private void initializeNadirPoint(int size) {
		this.nadirPoint = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
			this.nadirPoint.add(Double.NEGATIVE_INFINITY);
	}
	
	private void updateReferencePoint(S s) {
		for (int i = 0; i < s.getNumberOfObjectives(); i++) 
			this.referencePoint.set(i, Math.min(this.referencePoint.get(i),s.getObjective(i)));		
	}
	
	private void updateNadirPoint(S s) {
		for (int i = 0; i < s.getNumberOfObjectives(); i++)
			this.nadirPoint.set(i, Math.max(this.nadirPoint.get(i),s.getObjective(i)));
	}
	
	public void updateReferencePoint(List<S> population) {
		if (this.referencePoint==null) 
			initializeReferenceVector(this.getProblem().getNumberOfObjectives());
		
		for (S solution : population)
			this.updateReferencePoint(solution);
	}
	
	public void updateNadirPoint(List<S> population) {
		if (this.nadirPoint==null)
			initializeNadirPoint(this.getProblem().getNumberOfObjectives());
		
		for (S solution : population)
			this.updateNadirPoint(solution);
	}
	
	
	@Override
	public void specificMOEAComputations() {
		updateReferencePoint(this.getPopulation());
		updateNadirPoint(this.getPopulation());								
	}

	@Override
	protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
		// TODO Auto-generated method stub
		return null;
	}

}
