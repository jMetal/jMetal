package org.uma.jmetal.algorithm.impl.singleobjective.geneticalgorithm;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SinglePointCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.OneMax;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ajnebro on 26/10/14.
 */
public class GenerationalGeneticAlgorithm extends AbstractGeneticAlgorithm<List<Solution>> {
  private Comparator<Solution> comparator = new ObjectiveComparator(0) ;
  private int maxIterations ;
  private int populationSize ;

  private BinaryProblem problem = new OneMax(256) ;

  public GenerationalGeneticAlgorithm() {
    maxIterations = 250 ;
    populationSize = 100 ;
    crossoverOperator = new SinglePointCrossover.Builder()
            .setProbability(0.9)
            .build() ;

    mutationOperator = new BitFlipMutation.Builder()
            .setProbability(1.0 / problem.getNumberOfBits(0))
            .build();

    selectionOperator = new BinaryTournamentSelection.Builder()
            .build();

  }

  @Override
  protected boolean isStoppingConditionReached() {
    return (getIterations() >= maxIterations) ;
  }

  @Override
  protected List<Solution> createInitialPopulation() {
    List<Solution> population = new ArrayList<>(populationSize) ;
    for (int i = 0; i < populationSize; i++) {
      Solution<?> newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override
  protected List<Solution> replacement(List pop, List offspringPop) {
    pop.sort(comparator);
    offspringPop.add(pop.get(0)) ;
    offspringPop.add(pop.get(1)) ;
    offspringPop.sort(comparator);
    offspringPop.remove(offspringPop.size() - 1) ;
    offspringPop.remove(offspringPop.size() - 1) ;

    return offspringPop;
  }

  @Override
  protected List<Solution> reproduction(List matingPopulation) {
    List<Solution> offspringPopulation = new ArrayList<>(matingPopulation.size()+2) ;
    for (int i = 0; i < populationSize; i+=2) {
      List<Solution<?>> parents = new ArrayList<>(2);
      parents.add((Solution<?>)matingPopulation.get(i)) ;
      parents.add((Solution<?>)matingPopulation.get(i + 1));

      List<Solution<?>> offspring = (List<Solution<?>>) crossoverOperator.execute(parents);
      mutationOperator.execute(offspring.get(0)) ;
      mutationOperator.execute(offspring.get(1)) ;

      offspringPopulation.add(offspring.get(0)) ;
      offspringPopulation.add(offspring.get(1)) ;
    }
    return offspringPopulation ;
  }

  @Override
  protected List<Solution> selection(List population) {
    List<Solution> matingPopulation = new ArrayList<>(population.size()) ;
    for (int i = 0; i < populationSize; i++) {
      Solution<?> solution = (Solution<?>) selectionOperator.execute(population);
      matingPopulation.add(solution) ;
    }

    return matingPopulation;
  }

  @Override
  protected List<Solution> evaluatePopulation(List<Solution> population) {
    for (Solution solution : population) {
      problem.evaluate((BinarySolution)solution);
    }

    return population ;
  }

  @Override
  public List<Solution> getResult() {
    getPopulation().sort(comparator);
    List<Solution> result = new ArrayList<>(1) ;
    result.add(getPopulation().get(0));
    return result;
  }
}
