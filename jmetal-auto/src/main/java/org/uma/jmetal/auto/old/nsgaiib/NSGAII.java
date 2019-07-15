package org.uma.jmetal.auto.old.nsgaiib;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.RankingAndCrowdingSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.*;

import static org.uma.jmetal.auto.old.nsgaiib.NSGAII.Step.*;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class NSGAII<S extends Solution<?>> implements Algorithm<List<S>> {
	// XXX - Fields

	// Mutable data
	private List<S> population;
	private int evaluations;

	// Immutable data
	private final int populationSize;
	private final int offspringPopulationSize;
	private final int maxEvaluations;

	/*
	 * Immutable operators. Can be considered part of the state or not depending on
	 * the definitions we choose.
	 */
	private final Problem<S> problem;
	private final SelectionOperator<List<S>, S> selectionOperator;
	private final CrossoverOperator<S> crossoverOperator;
	private final MutationOperator<S> mutationOperator;
	private final SolutionListEvaluator<S> evaluator;
	private final Comparator<S> dominanceComparator;

	/* XXX - Constructor */

	public NSGAII(
			// Data
			int populationSize, int offspringPopulationSize, int maxEvaluations,
			// Operators
			Problem<S> problem, SelectionOperator<List<S>, S> selectionOperator, CrossoverOperator<S> crossoverOperator,
			MutationOperator<S> mutationOperator, SolutionListEvaluator<S> evaluator,
			Comparator<S> dominanceComparator) {
		// State
		this.problem = problem;
		this.populationSize = populationSize;
		this.offspringPopulationSize = offspringPopulationSize;
		this.maxEvaluations = maxEvaluations;

		// Operators
		this.selectionOperator = selectionOperator;
		this.crossoverOperator = crossoverOperator;
		this.mutationOperator = mutationOperator;
		this.evaluator = evaluator;
		this.dominanceComparator = dominanceComparator;
	}

	/* XXX - Methods required by the Algorithm interface */

	@Override
	public String getName() {
		return "NSGAII";
	}

	@Override
	public String getDescription() {
		return "Nondominated Sorting Genetic Algorithm version II";
	}

	/* XXX - Elements to make algorithm events observable */

	public static enum Step {
		INIT, SELECT, MATE, EVALUATE, REPLACE, DONE
	}

	public static interface Observer<S extends Solution<?>> {
		default void updatePopulation(List<S> population) {
		}

		default void updateNumberOfEvaluations(int evaluations) {
		}

		default void createSolution(S solution) {
		}

		default void updateStep(Step step) {
		}
	}

	private final Collection<Observer<S>> observers = new LinkedList<>();

	public void registerObserver(Observer<S> observer) {
		observers.add(observer);
	}

	public void unregisterObserver(Observer<S> observer) {
		observers.remove(observer);
	}

	private void notifyPopulation(List<S> population) {
		observers.forEach(observer -> observer.updatePopulation(population));
	}

	private void notifyEvaluations(int evaluations) {
		observers.forEach(observer -> observer.updateNumberOfEvaluations(evaluations));
	}

	private void notifySolution(S solution) {
		observers.forEach(observer -> observer.createSolution(solution));
	}

	private void notifyStep(Step step) {
		observers.forEach(observer -> observer.updateStep(step));
	}

	/* XXX - Observable run() */

	@Override
	public void run() {
		List<S> offspringPopulation;
		List<S> matingPopulation;

		notifyStep(INIT);
		population = createInitialPopulation();
		population = evaluatePopulation(population);
		notifyPopulation(population);
		population.forEach(solution -> notifySolution(solution));
		evaluations = populationSize;
		notifyEvaluations(evaluations);
		while (evaluations < maxEvaluations) {
			notifyStep(SELECT);
			matingPopulation = selection(population);
			notifyStep(MATE);
			offspringPopulation = reproduction(matingPopulation);
			notifyStep(EVALUATE);
			offspringPopulation = evaluatePopulation(offspringPopulation);
			offspringPopulation.forEach(solution -> notifySolution(solution));
			notifyStep(REPLACE);
			population = replacement(population, offspringPopulation);
			notifyPopulation(population);
			evaluations += offspringPopulationSize;
			notifyEvaluations(evaluations);
		}
		notifyStep(DONE);
	}

	@Override
	public List<S> getResult() {
		return SolutionListUtils.getNondominatedSolutions(population);
	}

	/* XXX - Methods required by the run() method */

	/**
	 * This method implements a default scheme generateConfigurationFile the initial population of
	 * genetic algorithm
	 * 
	 * @return
	 */
	private List<S> createInitialPopulation() {
		List<S> population = new ArrayList<>(populationSize);
		for (int i = 0; i < populationSize; i++) {
			S newIndividual = problem.createSolution();
			population.add(newIndividual);
		}
		return population;
	}

	private List<S> evaluatePopulation(List<S> population) {
		return evaluator.evaluate(population, problem);
	}

	/**
	 * This method iteratively applies a {@link SelectionOperator} to the population
	 * to fill the mating pool population.
	 *
	 * @param population
	 * @return The mating pool population
	 */
	private List<S> selection(List<S> population) {
		int matingPoolSize = offspringPopulationSize * crossoverOperator.getNumberOfRequiredParents();
		List<S> matingPopulation = new ArrayList<>(matingPoolSize);
		for (int i = 0; i < matingPoolSize; i++) {
			S solution = selectionOperator.execute(population);
			matingPopulation.add(solution);
		}

		return matingPopulation;
	}

	/**
	 * This methods iteratively applies a {@link CrossoverOperator} a
	 * {@link MutationOperator} to the population to generateConfigurationFile the offspring
	 * population. The population size must be divisible by the number of parents
	 * required by the {@link CrossoverOperator}; this way, the needed parents are
	 * taken sequentially from the population.
	 *
	 * The number of solutions returned by the {@link CrossoverOperator} must be
	 * equal to the offspringPopulationSize state variable
	 *
	 * @param matingPool
	 * @return The new created offspring population
	 */
	private List<S> reproduction(List<S> matingPool) {
		int numberOfParents = crossoverOperator.getNumberOfRequiredParents();

		List<S> offspringPopulation = new ArrayList<>(offspringPopulationSize);
		for (int i = 0; i < matingPool.size(); i += numberOfParents) {
			List<S> parents = new ArrayList<>(numberOfParents);
			for (int j = 0; j < numberOfParents; j++) {
				parents.add(population.get(i + j));
			}

			List<S> offspring = crossoverOperator.execute(parents);

			for (S s : offspring) {
				mutationOperator.execute(s);
				offspringPopulation.add(s);
				if (offspringPopulation.size() >= offspringPopulationSize)
					break;
			}
		}
		return offspringPopulation;
	}

	private List<S> replacement(List<S> population, List<S> offspringPopulation) {
		List<S> jointPopulation = new ArrayList<>();
		jointPopulation.addAll(population);
		jointPopulation.addAll(offspringPopulation);

		RankingAndCrowdingSelection<S> rankingAndCrowdingSelection;
		rankingAndCrowdingSelection = new RankingAndCrowdingSelection<S>(populationSize, dominanceComparator);

		return rankingAndCrowdingSelection.execute(jointPopulation);
	}
}
