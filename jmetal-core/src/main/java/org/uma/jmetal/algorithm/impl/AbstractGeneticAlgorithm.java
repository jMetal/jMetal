package org.uma.jmetal.algorithm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Abstract class representing a genetic algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public abstract class AbstractGeneticAlgorithm<S, Result> extends AbstractEvolutionaryAlgorithm<S, Result> {
  protected int maxPopulationSize ;
  protected SelectionOperator<List<S>, S> selectionOperator ;
  protected CrossoverOperator<S> crossoverOperator ;
  protected MutationOperator<S> mutationOperator ;

  /* Setters and getters */
  public void setMaxPopulationSize(int maxPopulationSize) {
    this.maxPopulationSize = maxPopulationSize ;
  }
  
  public int getMaxPopulationSize() {
    return maxPopulationSize ;
  }
  
  public SelectionOperator<List<S>, S> getSelectionOperator() {
    return selectionOperator;
  }

  public CrossoverOperator<S> getCrossoverOperator() {
    return crossoverOperator;
  }

  public MutationOperator<S> getMutationOperator() {
    return mutationOperator;
  }

  /**
   * Constructor
   * @param problem The problem to solve
   */
  public AbstractGeneticAlgorithm(Problem<S> problem) {
    setProblem(problem);
  }

  /**
   * This method implements a default scheme create the initial population of genetic algorithm
   * @return
   */
  protected List<S> createInitialPopulation() {
    @NotNull List<S> population = new ArrayList<>(getMaxPopulationSize());
    int bound = getMaxPopulationSize();
    for (int i = 0; i < bound; i++) {
      S solution = getProblem().createSolution();
      population.add(solution);
    }
    return population;
  }

  /**
   * This method iteratively applies a {@link SelectionOperator} to the population to fill the mating pool population.
   *
   * @param population
   * @return The mating pool population
   */
  @Override
  protected List<S> selection(List<S> population) {
    List<S> matingPopulation = new ArrayList<>(population.size());
    int bound = getMaxPopulationSize();
    for (int i = 0; i < bound; i++) {
      S execute = selectionOperator.execute(population);
      matingPopulation.add(execute);
    }

    return matingPopulation;
  }

  /**
   * This methods iteratively applies a {@link CrossoverOperator} a  {@link MutationOperator} to the population to
   * create the offspring population. The population size must be divisible by the number of parents required
   * by the {@link CrossoverOperator}; this way, the needed parents are taken sequentially from the population.
   *
   * No limits are imposed to the number of solutions returned by the {@link CrossoverOperator}.
   *
   * @param population
   * @return The new created offspring population
   */
  @Override
  protected List<S> reproduction(@NotNull List<S> population) {
    int numberOfParents = crossoverOperator.getNumberOfRequiredParents() ;

    checkNumberOfParents(population, numberOfParents);

    @NotNull List<S> offspringPopulation = new ArrayList<>(getMaxPopulationSize());
    for (int i = 0; i < getMaxPopulationSize(); i += numberOfParents) {
      List<S> parents = new ArrayList<>(numberOfParents);
      for (int j = 0; j < numberOfParents; j++) {
        parents.add(population.get(i+j));
      }

      List<S> offspring = crossoverOperator.execute(parents);

      for(S s: offspring){
        mutationOperator.execute(s);
        offspringPopulation.add(s);
      }
    }
    return offspringPopulation;
  }

  /**
   * A crossover operator is applied to a number of parents, and it assumed that the population contains
   * a valid number of solutions. This method checks that.
   * @param population
   * @param numberOfParentsForCrossover
   */
  protected void checkNumberOfParents(List<S> population, int numberOfParentsForCrossover) {
    if ((population.size() % numberOfParentsForCrossover) != 0) {
      throw new JMetalException("Wrong number of parents: the remainder if the " +
              "population size (" + population.size() + ") is not divisible by " +
              numberOfParentsForCrossover) ;
    }
  }
}
