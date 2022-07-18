package org.uma.jmetal.algorithm.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;

/**
 * Abstract class representing a Coral Reefs Optimization Algorithm
 * 
 * Reference: S. Salcedo-Sanz, J. Del Ser, S. Gil-López, I. Landa-Torres and J.
 * A. Portilla-Figueras, "The coral reefs optimization algorithm: an efficient
 * meta-heuristic for solving hard optimization problems," 15th Applied
 * Stochastic Models and Data Analysis International Conference, Mataró, Spain,
 * June, 2013.
 *
 * @author Inacio Medeiros <inaciogmedeiros@gmail.com>
 */
@SuppressWarnings("serial")
public abstract class AbstractCoralReefsOptimization<S, R>
		implements Algorithm<R> {

	protected List<S> population;
	protected List<Coordinate> coordinates;

	protected SelectionOperator<List<S>, S> selectionOperator;
	protected CrossoverOperator<S> crossoverOperator;
	protected MutationOperator<S> mutationOperator;
	protected Comparator<S> comparator;

	private int N, M; // Grid sizes
	private double rho; // Percentage of occupied reef
	private double Fbs, Fbr; // Percentage of broadcast spawners and brooders
	private double Fa, Fd; // Percentage of budders and depredated corals
	private double Pd; // Probability of depredation
	private int attemptsToSettle;

	/**
	 * Represents a Coordinate in Coral Reef Grid
	 * 
	 * @author inacio-medeiros
	 *
	 */
	public static class Coordinate implements Comparable<Coordinate> {
		private int x, y;

		/**
		 * Constructor
		 * 
		 * @param x
		 *            Coordinate's x-position
		 * @param y
		 *            Coordinate's y-position
		 */
		public Coordinate(int x, int y) {
			this.x = x;
			this.y = y;
		}

		/**
		 * Retrieves Coordinate's x-position
		 * 
		 * @return Coordinate's x-position
		 */
		public int getX() {
			return x;
		}

		/**
		 * Retrieves Coordinate's y-position
		 * 
		 * @return Coordinate's y-position
		 */
		public int getY() {
			return y;
		}

		/**
		 * Sets Coordinate's x-position to a new value
		 * 
		 * @param x
		 *            new value for Coordinate's x-position
		 */
		public void setX(int x) {
			this.x = x;
		}

		/**
		 * Sets Coordinate's y-position to a new value
		 * 
		 * @param x
		 *            new value for Coordinate's y-position
		 */
		public void setY(int y) {
			this.y = y;
		}

		@Override
		public int compareTo(@NotNull Coordinate arg0) {
			var diffX = Math.abs(arg0.x - this.x);
			var diffY = Math.abs(arg0.y - this.y);
			var result = Math.sqrt((diffX * diffX) + (diffY * diffY));

			return Integer.parseInt(Double.toString(result));
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			var other = (Coordinate) obj;

			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

	}

	public List<S> getPopulation() {
		return population;
	}

	public int getPopulationSize() {
		return population.size();
	}

	public List<Coordinate> getCoordinates() {
		return coordinates;
	}

	public void setPopulation(List<S> population) {
		this.population = population;
	}

	public void setCoordinates(List<Coordinate> coordinates) {
		this.coordinates = coordinates;
	}

	public int getN() {
		return N;
	}

	public int getM() {
		return M;
	}

	public double getRho() {
		return rho;
	}

	public double getFbs() {
		return Fbs;
	}

	public double getFbr() {
		return Fbr;
	}

	public double getFa() {
		return Fa;
	}

	public double getFd() {
		return Fd;
	}

	public double getPd() {
		return Pd;
	}

	public int getAttemptsToSettle() {
		return attemptsToSettle;
	}

	/**
	 * Constructor
	 * 
	 * @param comparator
	 *            Object for comparing two solutions
	 * @param selectionOperator
	 *            Selection Operator
	 * @param crossoverOperator
	 *            Crossover Operator
	 * @param mutationOperator
	 *            Mutation Operator
	 * @param n
	 *            width of Coral Reef Grid
	 * @param m
	 *            height of Coral Reef Grid
	 * @param rho
	 *            Percentage of occupied reef
	 * @param fbs
	 *            Percentage of broadcast spawners
	 * @param fa
	 *            Percentage of budders
	 * @param pd
	 *            Probability of depredation
	 * @param attemptsToSettle
	 *            number of attempts a larvae has to try to settle reef
	 */
	public AbstractCoralReefsOptimization(Comparator<S> comparator,
			SelectionOperator<List<S>, S> selectionOperator,
			CrossoverOperator<S> crossoverOperator,
			MutationOperator<S> mutationOperator, int n, int m, double rho,
			double fbs, double fa, double pd, int attemptsToSettle) {
		this.comparator = comparator;
		this.selectionOperator = selectionOperator;
		this.crossoverOperator = crossoverOperator;
		this.mutationOperator = mutationOperator;
		N = n;
		M = m;
		this.rho = rho;
		Fbs = fbs;
		Fbr = 1 - fbs;
		Fa = fa;
		Fd = fa;
		Pd = pd;
		this.attemptsToSettle = attemptsToSettle;
	}

	protected abstract void initProgress();

	protected abstract void updateProgress();

	protected abstract boolean isStoppingConditionReached();

	protected abstract List<S> createInitialPopulation();

	protected abstract List<Coordinate> generateCoordinates();

	protected abstract List<S> evaluatePopulation(List<S> population);

	protected abstract List<S> selectBroadcastSpawners(List<S> population);

	protected abstract List<S> sexualReproduction(List<S> broadcastSpawners);

	protected abstract List<S> asexualReproduction(List<S> brooders);

	protected abstract List<S> larvaeSettlementPhase(List<S> larvae,
			List<S> population, List<Coordinate> coordinates);

	protected abstract List<S> depredation(List<S> population,
			List<Coordinate> coordinates);

	@Override
	public void run() {
		List<S> broadcastSpawners;
		List<S> brooders;
		List<S> larvae;
		List<S> budders;

		population = createInitialPopulation();
		population = evaluatePopulation(population);

		coordinates = generateCoordinates();

		initProgress();
		while (!isStoppingConditionReached()) {
			broadcastSpawners = selectBroadcastSpawners(population);

			brooders = new ArrayList<S>((int) (Fbr * population.size()));

			for (var coral : population) {
				if (!broadcastSpawners.contains(coral)) {
					brooders.add(coral);
				}
			}

			larvae = sexualReproduction(broadcastSpawners);
			larvae = evaluatePopulation(larvae);

			population = larvaeSettlementPhase(larvae, population, coordinates);

			larvae = asexualReproduction(brooders);
			larvae = evaluatePopulation(larvae);

			population = larvaeSettlementPhase(larvae, population, coordinates);

			Collections.sort(population, comparator);

			budders = new ArrayList<S>(population.subList(0, (int) Fa * population.size()));

			population = larvaeSettlementPhase(budders, population, coordinates);

			Collections.sort(population, comparator);

			population = depredation(population, coordinates);

			updateProgress();
		}

	}

	@Override
	public abstract R getResult();

}
