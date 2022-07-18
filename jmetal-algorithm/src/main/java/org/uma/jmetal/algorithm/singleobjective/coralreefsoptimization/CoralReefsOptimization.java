package org.uma.jmetal.algorithm.singleobjective.coralreefsoptimization;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.impl.AbstractCoralReefsOptimization;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.pseudorandom.impl.MersenneTwisterGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author Inacio Medeiros <inaciogmedeiros@gmail.com>
 * 
 */
public class CoralReefsOptimization<S>
		extends AbstractCoralReefsOptimization<S, List<S>> {

	private Problem<S> problem;
	private int maxIterations;
	private int iterations;
	private MersenneTwisterGenerator random;

	public CoralReefsOptimization(Problem<S> problem,
                                  int maxIterations, Comparator<S> comparator,
                                  SelectionOperator<List<S>, S> selectionOperator,
                                  CrossoverOperator<S> crossoverOperator,
                                  MutationOperator<S> mutationOperator, int n, int m, double rho,
                                  double fbs, double fa, double pd, int attemptsToSettle) {

		super(comparator, selectionOperator, crossoverOperator,
				mutationOperator, n, m, rho, fbs, fa, pd, attemptsToSettle);

		this.problem = problem;
		this.maxIterations = maxIterations;
		this.random = new MersenneTwisterGenerator();

	}

	private static final long serialVersionUID = 3013223456538143239L;

	@Override
	protected void initProgress() {
		iterations = 0;
	}

	@Override
	protected void updateProgress() {
		iterations++;
	}

	@Override
	protected boolean isStoppingConditionReached() {
		return iterations == maxIterations;
	}

	@Override
	protected List<S> createInitialPopulation() {
		List<S> population;

		int quantity = (int) (getRho() * getN() * getM());

		@NotNull ArrayList<S> s = new ArrayList<>(getN() * getM());
		for (int i = 0; i < quantity; i++) {
			S solution = problem.createSolution();
			s.add(solution);
		}
		population = s;
		return population;
	}

	@Override
	protected @NotNull List<Coordinate> generateCoordinates() {
		int popSize = getPopulationSize();
		MersenneTwisterGenerator random = new MersenneTwisterGenerator();

		@NotNull ArrayList<Coordinate> coordinates = new ArrayList<>(popSize);
		for (int i = 0; i < popSize; i++) {
			Coordinate coordinate = new Coordinate(random.nextInt(0, getN() - 1),
					random.nextInt(0, getM() - 1));
			coordinates.add(coordinate);
		}

		return coordinates;
	}

	@Override
	protected @NotNull List<S> evaluatePopulation(@NotNull List<S> population) {
		for (int solution = 0; solution < population.size(); solution++) {
			this.problem.evaluate(population.get(solution));
		}
		return population;
	}

	@Override
	protected List<S> selectBroadcastSpawners(List<S> population) {
		int quantity = (int) (getFbs() * population.size());

		if ((quantity % 2) == 1) {
			quantity--;
		}

		List<S> spawners = new ArrayList<S>(quantity);

		for (int i = 0; i < quantity; i++) {
			S solution = selectionOperator.execute(population);
			spawners.add(solution);
		}

		return spawners;
	}

	@Override
	protected List<S> sexualReproduction(List<S> broadcastSpawners) {
		@NotNull List<S> parents = new ArrayList<S>(2);
		@NotNull List<S> larvae = new ArrayList<S>(broadcastSpawners.size() / 2);

		while (broadcastSpawners.size() > 0) {
			parents.add(selectionOperator.execute(broadcastSpawners));
			parents.add(selectionOperator.execute(broadcastSpawners));

			broadcastSpawners.remove(parents.get(0));

			if (broadcastSpawners.contains(parents.get(1))) {
				broadcastSpawners.remove(parents.get(1));
			}

			larvae.add(crossoverOperator.execute(parents).get(0));
			
			parents.clear();

		}

		return larvae;
	}

	@Override
	protected List<S> asexualReproduction(List<S> brooders) {
		int sz = brooders.size();

		@NotNull List<S> larvae = new ArrayList<>(sz);
		for (int i = 0; i < sz; i++) {
			S execute = mutationOperator.execute(brooders.get(i));
			larvae.add(execute);
		}

		return larvae;
	}

	@Override
	protected List<S> larvaeSettlementPhase(List<S> larvae, @NotNull List<S> population,
                                            List<Coordinate> coordinates) {

		int attempts = getAttemptsToSettle();
		int index;

		for (S larva : larvae) {

			for (int attempt = 0; attempt < attempts; attempt++) {
				Coordinate C = new Coordinate(random.nextInt(0, getN() - 1),
						random.nextInt(0, getM() - 1));

				if (!coordinates.contains(C)) {
					population.add(larva);
					coordinates.add(C);
					break;
				}

				index = coordinates.indexOf(C);

				if (comparator.compare(larva, population.get(index)) < 0) {
					population.add(index, larva);
					population.remove(index + 1);
					break;
				}

			}

		}

		return population;
	}

	@Override
	protected @NotNull List<S> depredation(@NotNull List<S> population,
                                           @NotNull List<Coordinate> coordinates) {
		int popSize = population.size();
		int quantity = (int) (getFd() * popSize);

		quantity = popSize - quantity;

		double coin;
		for (int i = popSize-1; i > quantity; i--) {
			coin = random.nextDouble();
			
			if(coin < getPd()){
				population.remove(population.size()-1);
				coordinates.remove(population.size()-1);	
			}
			
		}
				
		return population;
	}

	@Override
	public List<S> getResult() {
		Collections.sort(getPopulation(), comparator) ;
		return getPopulation();
	}

	@Override
	public String getName() {
		return "CRO";
	}

	@Override
	public @NotNull String getDescription() {
		return "Coral Reefs Optimizatoin";
	}

}
