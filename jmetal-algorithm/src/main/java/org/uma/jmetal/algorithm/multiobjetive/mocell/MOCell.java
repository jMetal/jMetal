package org.uma.jmetal.algorithm.multiobjetive.mocell;

import java.util.ArrayList;
import java.util.List;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 * 
 * @author JuanJo
 *
 * @param <S>
 */
public class MOCell<S extends Solution> extends AbstractGeneticAlgorithm<S, List<S>> {
	protected int evaluations;
	protected int maxEvaluations;
	protected int populationSize;
	protected final SolutionListEvaluator<S> evaluator;
	private int currentIndividual;  // Individual the algorithm looks at currently in the grid
	protected final Problem<S> problem;

	public MOCell(Problem<S> problem, int maxEvaluations, int populationSize,
		      CrossoverOperator<List<S>, List<S>> crossoverOperator, MutationOperator<S> mutationOperator,
		      SelectionOperator selectionOperator, SolutionListEvaluator<S> evaluator) {
		super();
	    this.problem = problem;
	    this.maxEvaluations = maxEvaluations;
	    this.populationSize = populationSize;

	    this.crossoverOperator = crossoverOperator;
	    this.mutationOperator = mutationOperator;
	    this.selectionOperator = selectionOperator;

	    this.evaluator = evaluator;
		
	}
	
	@Override
	protected void initProgress() {
		evaluations = 1;
	}

	@Override
	protected void updateProgress() {
		evaluations++;
		
	}

	@Override
	protected boolean isStoppingConditionReached() {
		return (evaluations==maxEvaluations);
	}

	@Override
	protected List<S> createInitialPopulation() {
	    List<S> population = new ArrayList<>(populationSize);
	    for (int i = 0; i < populationSize; i++) {
	      S newIndividual = problem.createSolution();
	      population.add(newIndividual);
	    }
	    return population;	
    }

	@Override
	protected List<S> evaluatePopulation(List<S> population) {
	    population = evaluator.evaluate(population, problem);

	    return population;
	}

	@Override
	protected List<S> selection(List<S> population) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<S> reproduction(List<S> population) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<S> replacement(List<S> population,
			List<S> offspringPopulation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<S> getResult() {
		// TODO Auto-generated method stub
		return null;
	}

}
