package org.uma.jmetal.algorithm.multiobjective.mocell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;
import org.uma.jmetal.util.solutionattribute.impl.LocationAttribute;

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
	
	private MOCellNeighborhood<S> neighborhood;
	private int currentIndividual;  // Individual the algorithm looks at currently in the grid
	private List<S> currentNeighbors;
	
	private CrowdingDistanceArchive<S> archive;
	protected final Problem<S> problem;
	
	private Comparator<Solution> dominanceComparator;
	private LocationAttribute<S> location;

	
	public MOCell(Problem<S> problem, int maxEvaluations, int populationSize,
		      CrossoverOperator<List<S>, List<S>> crossoverOperator, MutationOperator<S> mutationOperator,
		      SelectionOperator selectionOperator, SolutionListEvaluator<S> evaluator) {
		super();
	    this.problem = problem;
	    this.maxEvaluations = maxEvaluations;
	    this.populationSize = populationSize;
	    this.archive = new CrowdingDistanceArchive<>(this.populationSize);
	    this.neighborhood = new MOCellNeighborhood(this.populationSize);

	    
	    this.crossoverOperator = crossoverOperator;
	    this.mutationOperator = mutationOperator;
	    this.selectionOperator = selectionOperator;
	    this.dominanceComparator = new DominanceComparator();
	    		
	    		
	    this.evaluator = evaluator;	
	    
	}
	
	@Override
	protected void initProgress() {
		evaluations = 0;
		currentIndividual=0;
	}

	@Override
	protected void updateProgress() {
		evaluations++;
		currentIndividual=(currentIndividual+1)%populationSize;
		
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
	      archive.add(newIndividual);
	    }
	    location = new LocationAttribute<>(population);
	    return population;	
    }

	@Override
	protected List<S> evaluatePopulation(List<S> population) {
	    population = evaluator.evaluate(population, problem);
	    Iterator<S> it = population.iterator();
	    while (it.hasNext())
	    	archive.add(it.next());
	    return population;
	}

	@Override
	protected List<S> selection(List<S> population) {
	    List<S> parents = new ArrayList<>(2);
	    currentNeighbors = neighborhood.getEightNeighbors(population, currentIndividual);
        currentNeighbors.add(population.get(currentIndividual));
        
	    parents.add(selectionOperator.execute(currentNeighbors));
        if (archive.size() > 0) {
          parents.add(selectionOperator.execute(archive.getSolutionList()));
        } else {                   
          parents.add(selectionOperator.execute(currentNeighbors));
        }        
        return parents;
	}

	@Override
	protected List<S> reproduction(List<S> population) {
		List<S> result = new ArrayList<>(1);
		List<S> offspring = crossoverOperator.execute(population);
		mutationOperator.execute(offspring.get(0));
		result.add(offspring.get(0));
		return result;
	}

	@Override
	protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
		int flag = dominanceComparator.compare(population.get(currentIndividual),offspringPopulation.get(0));

        if (flag == 1) { //The new individual dominates
          insertNewIndividualWhenDominates(population,offspringPopulation);                  
        } else if (flag == 0) { //The new individual is non-dominated               
           insertNewIndividualWhenNonDominated(population,offspringPopulation);                                
        }
		return population;
	}

	@Override
	public List<S> getResult() {
		return archive.getSolutionList();
	}

	private void insertNewIndividualWhenDominates(List<S> population, List<S> offspringPopulation) {
		location.setAttribute(offspringPopulation.get(0), 
									   location.getAttribute(population.get(currentIndividual)));

		population.set(location.getAttribute(offspringPopulation.get(0)),offspringPopulation.get(0));
		archive.add(offspringPopulation.get(0));
	}
	
	
	private void insertNewIndividualWhenNonDominated(List<S> population, List<S> offspringPopulation) {
		currentNeighbors.add(offspringPopulation.get(0));               
        location.setAttribute(offspringPopulation.get(0), -1);
        
        Ranking rank = new DominanceRanking();
        rank.computeRanking(currentNeighbors);
        
        CrowdingDistance crowdingDistance = new CrowdingDistance();
        for (int j = 0; j < rank.getNumberOfSubfronts(); j++) {
        	crowdingDistance.computeDensityEstimator(rank.getSubfront(j));  
        }
       
        Collections.sort(this.currentNeighbors,new RankingAndCrowdingDistanceComparator());
        S worst = this.currentNeighbors.get(this.currentNeighbors.size()-1);

        if (location.getAttribute(worst) == -1) { //The worst is the offspring
          archive.add(offspringPopulation.get(0));
        } else {
      	  location.setAttribute(offspringPopulation.get(0), 
      			  						location.getAttribute(worst));
      	  population.set(location.getAttribute(offspringPopulation.get(0)),offspringPopulation.get(0));      	  
      	  archive.add(offspringPopulation.get(0));
        }      
	}
}
