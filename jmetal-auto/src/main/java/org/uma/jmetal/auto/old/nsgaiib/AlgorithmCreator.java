package org.uma.jmetal.auto.old.nsgaiib;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.Comparator;
import java.util.List;

public interface AlgorithmCreator<T> {
	
	// XXX - Definition
	
	T create(String[] args);

	// XXX - Specific algorithms
	
	@SuppressWarnings("unchecked")
	public static AlgorithmCreator<NSGAII<Solution<Object>>> NSGA2 = args -> {
		// Data
		int populationSize = Parameter.POPULATION_SIZE.getFrom(args);
		int offspringPopulationSize = Parameter.OFFSPRING_POPULATION_SIZE.getFrom(args);
		int maxEvaluations = Parameter.MAX_EVALUATIONS.getFrom(args);
		
		/*
		 * Operators. Since the instantiation is dynamic (done at runtime), generics
		 * have no effect whatsoever due to type erasure, so we can do what we want.
		 * Here we cast them to the most abstract types to enforce broad compatibility.
		 * The generics of the returned algorithm will also be of the most abstract type
		 * automatically.
		 */
		Problem<Solution<Object>> problem = (Problem<Solution<Object>>) Parameter.PROBLEM.getFrom(args);
		SelectionOperator<List<Solution<Object>>, Solution<Object>> selectionOperator = (SelectionOperator<List<Solution<Object>>, Solution<Object>>) Parameter.SELECTION_OPERATOR.getFrom(args);
		CrossoverOperator<Solution<Object>> crossoverOperator = (CrossoverOperator<Solution<Object>>) Parameter.CROSSOVER_OPERATOR.getFrom(args);
		MutationOperator<Solution<Object>> mutationOperator = (MutationOperator<Solution<Object>>) Parameter.MUTATION_OPERATOR.getFrom(args);
		SolutionListEvaluator<Solution<Object>> evaluator = (SolutionListEvaluator<Solution<Object>>) Parameter.EVALUATOR.getFrom(args);
		Comparator<Solution<Object>> dominanceComparator = (Comparator<Solution<Object>>) Parameter.DOMINANCE_COMPARATOR.getFrom(args);
		
		return new NSGAII<>(
				// Data
				populationSize, offspringPopulationSize, maxEvaluations,
				// Operators
				problem, selectionOperator, crossoverOperator, mutationOperator, evaluator, dominanceComparator);
	};
}
