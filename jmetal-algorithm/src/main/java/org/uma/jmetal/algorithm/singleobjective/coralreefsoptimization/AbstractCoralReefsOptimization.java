package org.uma.jmetal.algorithm.singleobjective.coralreefsoptimization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.solution.Solution;

public abstract class AbstractCoralReefsOptimization<S extends Solution<?>, R>
		implements Algorithm<R> {
	
	private static final long serialVersionUID = -6817687299172531643L;

	public class Coordinate implements Comparable<Coordinate> {
		private int x, y;
		
		public Coordinate(int x, int y){
			this.x = x;
			this.y = y;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public void setX(int x) {
			this.x = x;
		}

		public void setY(int y) {
			this.y = y;
		}

		@Override
		public int compareTo(Coordinate arg0) {			
			int diffX = Math.abs(arg0.x - this.x);
			int diffY = Math.abs(arg0.y - this.y);
			double result = Math.sqrt((diffX*diffX) + (diffY*diffY)); 
			
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
			Coordinate other = (Coordinate) obj;
			
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

	}

	private List<S> population;
	private List<Coordinate> coordinates;
	
	public List<S> getPopulation() {
		return population;
	}
	
	public int getPopulationSize(){
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
	
	public AbstractCoralReefsOptimization(
			Comparator<S> comparator,
			SelectionOperator<List<S>, S> selectionOperator,
			CrossoverOperator<S> crossoverOperator,
			MutationOperator<S> mutationOperator, int n, int m, double rho,
			double fbs, double fa, double pd,
			int attemptsToSettle) {
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

	protected abstract List<S> larvaeSetting(List<S> larvae, List<S> population, List<Coordinate> coordinates);

	protected abstract List<S> depredation(List<S> population, List<Coordinate> coordinates);

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
	    	
	    	for(S coral : population){
	    		if(!broadcastSpawners.contains(coral)){
	    			brooders.add(coral);
	    		}
	    	}
	    	
	    	larvae = sexualReproduction(broadcastSpawners);
	    	larvae = evaluatePopulation(larvae);
	    	
	    	population = larvaeSetting(larvae, population, coordinates);
	    	
	    	larvae = asexualReproduction(brooders);
	    	larvae = evaluatePopulation(larvae);
	    	
	    	population = larvaeSetting(larvae, population, coordinates);
	    	
	    	Collections.sort(population,comparator);
	    	
	    	budders = new ArrayList<S>((int) (Fa * population.size()));
	    	for(int i = 0; i < budders.size(); i++){
	    		budders.add(population.get(i));
	    	}
	    	
	    	population = larvaeSetting(budders, population, coordinates);
	    	
	    	Collections.sort(population,comparator);
	    	
	    	population = depredation(population, coordinates);
	    	
	    	updateProgress();
	    }
		
	}

	@Override
	public abstract R getResult();

}
