package org.uma.jmetal.algorithm.multiobjective.nsgaiii;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.util.EnvironmentalSelection;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.util.ReferencePoint;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;

import java.util.List;
import java.util.Vector;

/**
 * Created by ajnebro on 30/10/14.
 * Modified by Juanjo on 13/11/14
 *
 * This implementation is based on the code of Tsung-Che Chiang
 * http://web.ntnu.edu.tw/~tcchiang/publications/nsga3cpp/nsga3cpp.htm
 */
@SuppressWarnings("serial")
public class NSGAIII<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {
  protected int iterations ;
  protected int maxIterations ;
  protected int populationSize ;

  protected Problem<S> problem ;

  protected SolutionListEvaluator<S> evaluator ;

  private Vector<Integer> numberOfDivisions  ;
  private List<ReferencePoint<S>> referencePoints = new Vector<>() ;
  
  public NSGAIIIBuilder<S> Builder;
  

  /** Constructor */
  public NSGAIII() {
  }

  /** Constructor */
  NSGAIII(NSGAIIIBuilder<S> builder) { // can be created from the NSGAIIIBuilder within the same package
    problem = builder.problem ;
    maxIterations = builder.maxIterations ;
    

    crossoverOperator =  builder.crossoverOperator ;
    mutationOperator  =  builder.mutationOperator ;
    selectionOperator =  builder.selectionOperator ;

    evaluator = builder.evaluator ;

    /// NSGAIII
    numberOfDivisions = new Vector<>(1) ;
    numberOfDivisions.add(12) ; // Default value for 3D problems


    (new ReferencePoint<S>()).generateReferencePoints(referencePoints,problem.getNumberOfObjectives() , numberOfDivisions); 
    
    
    populationSize = referencePoints.size();
    System.out.println(referencePoints.size());
    while (populationSize%4>0) populationSize++;

    
    JMetalLogger.logger.info("rpssize: " + referencePoints.size()); ;
  }

  @Override
  protected void initProgress() {
    iterations = 1 ;
  }

  @Override
  protected void updateProgress() {
    iterations++ ;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return iterations >= maxIterations;
  }

  @Override
  protected List<S> createInitialPopulation() {
    List<S> population = new ArrayList<>(populationSize) ;
    for (int i = 0; i < populationSize; i++) {
      S newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override
  protected List<S> evaluatePopulation(List<S> population) {
    population = evaluator.evaluate(population, problem) ;

    return population ;
  }

  @Override
  protected List<S> selection(List<S> population) {
    List<S> matingPopulation = new ArrayList<>(population.size()) ;
    for (int i = 0; i < populationSize; i++) {
      S solution = selectionOperator.execute(population);
      matingPopulation.add(solution) ;
    }

    return matingPopulation;
  }

  @Override
  protected List<S> reproduction(List<S> population) {
    List<S> offspringPopulation = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i+=2) {
      List<S> parents = new ArrayList<>(2);
      parents.add(population.get(i));
      parents.add(population.get(i+1));

      List<S> offspring = crossoverOperator.execute(parents);

      mutationOperator.execute(offspring.get(0));
      mutationOperator.execute(offspring.get(1));

      offspringPopulation.add(offspring.get(0));
      offspringPopulation.add(offspring.get(1));
    }
    return offspringPopulation ;
  }

  
  private List<ReferencePoint<S>> getReferencePointsCopy() {
	  List<ReferencePoint<S>> copy = new ArrayList<>();
	  for (ReferencePoint<S> r : this.referencePoints) {
		  copy.add(new ReferencePoint<>(r));
	  }
	  return copy;
  }
  
  @Override
  protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
   
	List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population) ;
    jointPopulation.addAll(offspringPopulation) ;

    Ranking<S> ranking = computeRanking(jointPopulation);
    
    //List<Solution> pop = crowdingDistanceSelection(ranking);
    List<S> pop = new ArrayList<>();
    List<List<S>> fronts = new ArrayList<>();
    int rankingIndex = 0;
    int candidateSolutions = 0;
    while (candidateSolutions < populationSize) {
      fronts.add(ranking.getSubfront(rankingIndex));
      candidateSolutions += ranking.getSubfront(rankingIndex).size();
      if ((pop.size() + ranking.getSubfront(rankingIndex).size()) <= populationSize) 
        addRankedSolutionsToPopulation(ranking, rankingIndex, pop);
      rankingIndex++;
    }
    
    // A copy of the reference list should be used as parameter of the environmental selection
    EnvironmentalSelection<S> selection = new EnvironmentalSelection<>(fronts,populationSize,getReferencePointsCopy(),problem.getNumberOfObjectives());
    
    pop = selection.execute(pop);
     
    return pop;
  }

  @Override
  public List<S> getResult() {
    return getNonDominatedSolutions(getPopulation()) ;
  }



  protected Ranking<S> computeRanking(List<S> solutionList) {
    Ranking<S> ranking = new DominanceRanking<>() ;
    ranking.computeRanking(solutionList) ;

    return ranking ;
  }

  protected void addRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
    List<S> front ;

    front = ranking.getSubfront(rank);

    for (int i = 0 ; i < front.size(); i++) {
      population.add(front.get(i));
    }
  }

  protected List<S> getNonDominatedSolutions(List<S> solutionList) {
    return SolutionListUtils.getNondominatedSolutions(solutionList) ;
  }
}
