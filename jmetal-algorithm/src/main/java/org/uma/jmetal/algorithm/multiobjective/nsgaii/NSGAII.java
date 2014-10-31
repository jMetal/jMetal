package org.uma.jmetal.algorithm.multiobjective.nsgaii;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajnebro on 30/10/14.
 */
public class NSGAII extends AbstractGeneticAlgorithm<List<Solution>> {
  protected int iterations ;
  protected int maxIterations ;
  protected int populationSize ;

  protected Problem problem ;

  protected SolutionListEvaluator evaluator ;
  /** Constructor */
  public NSGAII() {
  }

  /** Constructor */
  private NSGAII(Builder builder) {
    problem = builder.problem ;
    maxIterations = builder.maxIterations ;
    populationSize = builder.populationSize ;

    crossoverOperator = builder.crossoverOperator ;
    mutationOperator = builder.mutationOperator ;
    selectionOperator = builder.selectionOperator ;

    evaluator = builder.evaluator ;
  }

  /** Builder class */
  public static class Builder {
    private Problem problem ;
    private int maxIterations ;
    private int populationSize ;
    private CrossoverOperator crossoverOperator ;
    private MutationOperator mutationOperator ;
    private SelectionOperator selectionOperator ;
    private SolutionListEvaluator evaluator ;

    /** Builder constructor */
    public Builder(Problem problem) {
      this.problem = problem ;
      maxIterations = 250 ;
      populationSize = 100 ;
      evaluator = new SequentialSolutionListEvaluator() ;
    }

    public Builder setMaxIterations(int maxIterations) {
      this.maxIterations = maxIterations ;

      return this ;
    }

    public Builder setPopulationSize(int populationSize) {
      this.populationSize = populationSize ;

      return this ;
    }

    public Builder setCrossoverOperator(CrossoverOperator crossoverOperator) {
      this.crossoverOperator = crossoverOperator ;

      return this ;
    }

    public Builder setMutationOperator(MutationOperator mutationOperator) {
      this.mutationOperator = mutationOperator ;

      return this ;
    }

    public Builder setSelectionOperator(SelectionOperator selectionOperator) {
      this.selectionOperator = selectionOperator ;

      return this ;
    }

    public Builder setSolutionListEvaluator(SolutionListEvaluator evaluator) {
      this.evaluator = evaluator ;

      return this ;
    }

    public NSGAII build() {
      return new NSGAII(this) ;
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

  @Override
  protected List<Solution> replacement(List<Solution> population, List<Solution> offspringPopulation) {
    List<Solution> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population) ;
    jointPopulation.addAll(offspringPopulation) ;

    Ranking ranking = computeRanking(jointPopulation);
    List<Solution> pop = crowdingDistanceSelection(ranking);

    return pop;
  }

  @Override
  public List<Solution> getResult() {
    return getNonDominatedSolutions(getPopulation()) ;
  }

////////////// TODO: to be integrated smoothly

  protected Ranking computeRanking(List<Solution> solutionList) {
    Ranking ranking = new DominanceRanking() ;
    ranking.computeRanking(solutionList) ;

    return ranking ;
  }

  protected List<Solution> crowdingDistanceSelection(Ranking ranking) {
    CrowdingDistance crowdingDistance = new CrowdingDistance() ;
    List<Solution> population = new ArrayList<>(populationSize) ;
    int rankingIndex = 0;
    while (populationIsNotFull(population)) {
      if (subfrontFillsIntoThePopulation(ranking, rankingIndex, population)) {
        addRankedSolutionsToPopulation(ranking, rankingIndex, population);
        rankingIndex++;
      } else {
        crowdingDistance.computeDensityEstimator(ranking.getSubfront(rankingIndex));
        addLastRankedSolutionsToPopulation(ranking, rankingIndex, population);
      }
    }

    return population ;
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

  protected void addLastRankedSolutionsToPopulation(Ranking ranking, int rank, List<Solution>population) {
    List<Solution> currentRankedFront = ranking.getSubfront(rank) ;

    Collections.sort(currentRankedFront, new CrowdingDistanceComparator()) ;

    int i = 0 ;
    while (population.size() < populationSize) {
      population.add(currentRankedFront.get(i)) ;
      i++ ;
    }
  }

  protected List<Solution> getNonDominatedSolutions(List<Solution> solutionList) {
    Ranking ranking = new DominanceRanking() ;
    return ranking.computeRanking(solutionList).getSubfront(0);
  }
}
