package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.variation.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.variation.Variation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;

/** @param <S> */
public class ParallelCrossoverAndMutationVariation<S extends Solution<?>> implements Variation<S> {
  private CrossoverOperator<S> crossover;
  private MutationOperator<S> mutation;
  private int matingPoolSize;
  private int offspringPopulationSize;

  public ParallelCrossoverAndMutationVariation(
      int offspringPopulationSize, CrossoverOperator<S> crossover, MutationOperator<S> mutation) {
    this.crossover = crossover;
    this.mutation = mutation;
    this.offspringPopulationSize = offspringPopulationSize;

    this.matingPoolSize =
        offspringPopulationSize
            * crossover.getNumberOfRequiredParents()
            / crossover.getNumberOfGeneratedChildren();

    var remainder = matingPoolSize % crossover.getNumberOfRequiredParents();
    if (remainder != 0) {
      matingPoolSize += remainder;
    }
  }

  @Override
  public List<S> variate(List<S> population, List<S> matingPopulation) {
    var numberOfParents = crossover.getNumberOfRequiredParents();

    checkNumberOfParents(matingPopulation, numberOfParents);

    @NotNull List<List<S>> parentsList = new ArrayList<>();

    for (var i = 0; i < matingPoolSize; i += numberOfParents) {
      List<S> parents = new ArrayList<>(numberOfParents);
      for (var j = 0; j < numberOfParents; j++) {
        parents.add(matingPopulation.get(i + j));
      }
      parentsList.add(parents);
    }

    var offspringPopulation = parentsList.stream().flatMap(parents -> {
      var offspring = crossover.execute(parents);
        for (var solution : offspring) {
            mutation.execute(solution);
        }

        return offspring.stream() ;
    }).collect(Collectors.toList()) ;

    while (offspringPopulation.size() > offspringPopulationSize) {
      offspringPopulation.remove(offspringPopulation.size() - 1);
    }

    Check.that(
        offspringPopulation.size() == offspringPopulationSize,
        "The size of the"
            + "offspring population is not correct: "
            + offspringPopulation.size()
            + " instead of "
            + offspringPopulationSize);
    return offspringPopulation;
  }

  /**
   * A crossover operator is applied to a number of parents, and it assumed that the population
   * contains a valid number of population. This method checks that.
   *
   * @param population
   * @param numberOfParentsForCrossover
   */
  private void checkNumberOfParents(@NotNull List<S> population, int numberOfParentsForCrossover) {
    if ((population.size() % numberOfParentsForCrossover) != 0) {
      throw new JMetalException(
          "Wrong number of parents: the remainder if the "
              + "population size ("
              + population.size()
              + ") is not divisible by "
              + numberOfParentsForCrossover);
    }
  }

  @Override
  public int getMatingPoolSize() {
    return matingPoolSize;
  }

  @Override
  public int getOffspringPopulationSize() {
    return offspringPopulationSize;
  }
}
