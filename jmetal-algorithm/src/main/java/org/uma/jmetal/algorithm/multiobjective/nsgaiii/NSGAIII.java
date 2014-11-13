package org.uma.jmetal.algorithm.multiobjective.nsgaiii;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.EnvironmentalSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * Created by ajnebro on 30/10/14.
 * Modified by Juanjo Durillo on 13/11/14
 * This implementation is based on the code of Tsung-Che Chiang
 * http://web.ntnu.edu.tw/~tcchiang/publications/nsga3cpp/nsga3cpp.htm
 */
public class NSGAIII extends AbstractGeneticAlgorithm<Solution, List<Solution>> {
  protected int iterations ;
  protected int maxIterations ;
  protected int populationSize ;

  protected Problem problem ;

  protected SolutionListEvaluator<Solution> evaluator ;

  private Vector<Integer> numberOfDivisions  ;
  private List<ReferencePoint> referencePoints = new Vector<>() ;
  
  public static BuilderNSGAIII Builder;
  

  /** Constructor */
  public NSGAIII() {
  }

  /** Constructor */
  NSGAIII(BuilderNSGAIII builder) { // can be created from the BuilderNSGAIII within the same package
    problem = builder.problem ;
    maxIterations = builder.maxIterations ;
    

    crossoverOperator =  builder.crossoverOperator ;
    mutationOperator  =  builder.mutationOperator ;
    selectionOperator =  builder.selectionOperator ;

    evaluator = builder.evaluator ;

    /// NSGAIII
    numberOfDivisions = new Vector<>(1) ;
    numberOfDivisions.add(builder.getDivisions()) ; // Default value for 3D problems


    ReferencePoint.generateReferencePoints(referencePoints, problem.getNumberOfObjectives() , numberOfDivisions);
    
    populationSize = referencePoints.size();
    while (populationSize%4>0) populationSize++;

    
    // REMOVE THIS CODE ANTONIO PUT HERE FOR DEBUGGING??
    for (int i = 0; i < referencePoints.size(); i++) {
      for (int j = 0 ; j < referencePoints.get(i).position.size(); j++) {
      }
    }
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
  protected List<Solution> createInitialPopulation() {
    List<Solution> population = new ArrayList<>(populationSize) ;
    for (int i = 0; i < populationSize; i++) {
      Solution newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override
  protected List<Solution> evaluatePopulation(List<Solution> population) {
    population = evaluator.evaluate(population, problem) ;

    return population ;
  }

  @Override
  protected List<Solution> selection(List<Solution> population) {
    List<Solution> matingPopulation = new ArrayList<>(population.size()) ;
    for (int i = 0; i < populationSize; i++) {
      Solution solution = selectionOperator.execute(population);
      matingPopulation.add(solution) ;
    }

    return matingPopulation;
  }

  @Override
  protected List<Solution> reproduction(List<Solution> population) {
    List<Solution> offspringPopulation = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i+=2) {
      List<Solution> parents = new ArrayList<>(2);
      parents.add(population.get(i));
      parents.add(population.get(i+1));

      List<Solution> offspring = crossoverOperator.execute(parents);

      mutationOperator.execute(offspring.get(0));
      mutationOperator.execute(offspring.get(1));

      offspringPopulation.add(offspring.get(0));
      offspringPopulation.add(offspring.get(1));
    }
    return offspringPopulation ;
  }

  
  private List<ReferencePoint> getReferencePointsCopy() {
	  List<ReferencePoint> copy = new ArrayList<>();
	  for (ReferencePoint r : this.referencePoints) {
		  copy.add(new ReferencePoint(r));
	  }
	  return copy;
  }
  
  @Override
  protected List<Solution> replacement(List<Solution> population, List<Solution> offspringPopulation) {
   
	List<Solution> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population) ;
    jointPopulation.addAll(offspringPopulation) ;

    Ranking ranking = computeRanking(jointPopulation);
    
    //List<Solution> pop = crowdingDistanceSelection(ranking);
    List<Solution> pop = new ArrayList<>();
    List<List<Solution>> fronts = new ArrayList<>();
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
    
    
    EnvironmentalSelection selection = new EnvironmentalSelection.Builder()
    													 .setNumberOfObjectives(problem.getNumberOfObjectives())
    													 .setFronts(fronts)
    													 .setSolutionsToSelect(populationSize)
    													 .setReferencePoints(getReferencePointsCopy())
    													 .build();
    
    pop = selection.execute(pop);
     
    return pop;
  }

  @Override
  public List<Solution> getResult() {
    return getNonDominatedSolutions(getPopulation()) ;
  }



  protected Ranking computeRanking(List<Solution> solutionList) {
    Ranking ranking = new DominanceRanking() ;
    ranking.computeRanking(solutionList) ;

    return ranking ;
  }

  protected boolean populationIsNotFull(List<Solution> population) {
    return population.size() < populationSize;
  }

  protected boolean subfrontFillsIntoThePopulation(Ranking ranking, int rank, List<Solution> population) {
    return ranking.getSubfront(rank).size() < (populationSize - population.size()) ;
  }

  protected void addRankedSolutionsToPopulation(Ranking ranking, int rank, List<Solution> population) {
    List<Solution> front ;

    front = ranking.getSubfront(rank);

    for (int i = 0 ; i < front.size(); i++) {
      population.add(front.get(i));
    }
  }

  protected List<Solution> getNonDominatedSolutions(List<Solution> solutionList) {
    return SolutionListUtils.getNondominatedSolutions(solutionList) ;
  }
}
