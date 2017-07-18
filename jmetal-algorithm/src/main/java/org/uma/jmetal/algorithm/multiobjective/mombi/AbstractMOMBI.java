package org.uma.jmetal.algorithm.multiobjective.mombi;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing variants of the MOMBI algorithm
 * @author Juan J. Durillo
 * Modified by Antonio J. Nebro
 *
 * @param <S>
 */
@SuppressWarnings("serial")
public abstract class AbstractMOMBI<S extends Solution<?>> extends AbstractGeneticAlgorithm<S,List<S>> {
	protected final int maxIterations;

	protected int iterations = 0;
	protected final SolutionListEvaluator<S> evaluator;
	protected final List<Double> referencePoint;
	protected final List<Double> nadirPoint;

	/**
	 * Constructor
	 *
	 * @param problem Problem to be solved
	 * @param maxIterations Maximum number of iterations the algorithm
	 * will perform
	 * @param crossover Crossover operator
	 * @param mutation Mutation operator
	 * @param selection Selection operator
	 * @param evaluator Evaluator object for evaluating solution lists
	 */
	public AbstractMOMBI(Problem<S> problem, int maxIterations,
											 CrossoverOperator<S> crossover, MutationOperator<S> mutation,
											 SelectionOperator<List<S>,S> selection,
											 SolutionListEvaluator<S> evaluator) {
		super(problem);
		this.maxIterations = maxIterations;

		this.crossoverOperator 	= crossover;
		this.mutationOperator  	= mutation;
		this.selectionOperator  = selection;

		this.evaluator = evaluator;

		this.nadirPoint     = new ArrayList<Double>(this.getProblem().getNumberOfObjectives());
		this.initializeNadirPoint(this.getProblem().getNumberOfObjectives());
		this.referencePoint = new ArrayList<Double>(this.getProblem().getNumberOfObjectives());
		this.initializeReferencePoint(this.getProblem().getNumberOfObjectives());
	}

	@Override
	protected void initProgress() {
		this.iterations = 1;
	}

	@Override
	protected void updateProgress() {
		this.iterations+=1;
	}

	@Override
	protected boolean isStoppingConditionReached() {
		return this.iterations >= this.maxIterations;
	}

	@Override
	protected List<S> evaluatePopulation(List<S> population) {
		population = evaluator.evaluate(population, getProblem());

		return population;
	}

	@Override
	protected List<S> selection(List<S> population) {
		List<S> matingPopulation = new ArrayList<>(population.size());
		for (int i = 0; i < this.getMaxPopulationSize(); i++) {
			S solution = selectionOperator.execute(population);
			matingPopulation.add(solution);
		}

		return matingPopulation;
	}

	@Override
	protected List<S> reproduction(List<S> population) {
		List<S> offspringPopulation = new ArrayList<>(this.getMaxPopulationSize());
		for (int i = 0; i < this.getMaxPopulationSize(); i += 2) {
			List<S> parents = new ArrayList<>(2);
			int parent1Index = JMetalRandom.getInstance().nextInt(0, this.getMaxPopulationSize()-1);
			int parent2Index = JMetalRandom.getInstance().nextInt(0, this.getMaxPopulationSize()-1);
			while (parent1Index==parent2Index)
				parent2Index = JMetalRandom.getInstance().nextInt(0, this.getMaxPopulationSize()-1);
			parents.add(population.get(parent1Index));
			parents.add(population.get(parent2Index));

			List<S> offspring = crossoverOperator.execute(parents);

			mutationOperator.execute(offspring.get(0));
			mutationOperator.execute(offspring.get(1));

			offspringPopulation.add(offspring.get(0));
			offspringPopulation.add(offspring.get(1));
		}
		return offspringPopulation;
	}

	@Override
	public List<S> getResult() {
		this.setPopulation(evaluator.evaluate(this.getPopulation(), getProblem()));

		return this.getPopulation();
	}

	@Override
	public void run() {
		List<S> offspringPopulation;
		List<S> matingPopulation;

		this.setPopulation(createInitialPopulation());
		this.evaluatePopulation(this.getPopulation());
		initProgress();
		//specific GA needed computations
		this.specificMOEAComputations();
		while (!isStoppingConditionReached()) {
			matingPopulation = selection(this.getPopulation());
			offspringPopulation = reproduction(matingPopulation);
			offspringPopulation = evaluatePopulation(offspringPopulation);
			this.setPopulation(replacement(this.getPopulation(), offspringPopulation));
			updateProgress();
			// specific GA needed computations
			this.specificMOEAComputations();
		}
	}

	public abstract void specificMOEAComputations();

	public List<Double> getReferencePoint() {
		return this.referencePoint;
	}

	public List<Double> getNadirPoint() {
		return this.nadirPoint;
	}

	private void initializeReferencePoint(int size) {
		for (int i = 0; i < size; i++)
			this.getReferencePoint().add(Double.POSITIVE_INFINITY);
	}

	private void initializeNadirPoint(int size) {
		for (int i = 0; i < size; i++)
			this.getNadirPoint().add(Double.NEGATIVE_INFINITY);
	}

	protected void updateReferencePoint(S s) {
		for (int i = 0; i < s.getNumberOfObjectives(); i++)
			this.getReferencePoint().set(i, Math.min(this.getReferencePoint().get(i),s.getObjective(i)));
	}

	protected void updateNadirPoint(S s) {
		for (int i = 0; i < s.getNumberOfObjectives(); i++)
			this.getNadirPoint().set(i, Math.max(this.getNadirPoint().get(i),s.getObjective(i)));
	}

	public void updateReferencePoint(List<S> population) {
		for (S solution : population)
			this.updateReferencePoint(solution);
	}

	public void updateNadirPoint(List<S> population) {
		for (S solution : population)
			this.updateNadirPoint(solution);
	}

	

	protected boolean populationIsNotFull(List<S> population) {
		return population.size() < getMaxPopulationSize();
	}

	protected void setReferencePointValue(Double value, int index) {
		if ((index < 0) || (index >= this.referencePoint.size())) {
			throw new IndexOutOfBoundsException();
		}

		this.referencePoint.set(index, value);
	}
}
