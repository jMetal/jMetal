package org.uma.jmetal.auto.component.variation.impl;

import org.uma.jmetal.auto.component.variation.Variation;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MOEADVariation implements Variation<DoubleSolution> {
  private DifferentialEvolutionCrossover crossover ;
  private MutationOperator<DoubleSolution> mutation ;
  private int matingPoolSize ;
  private int offspringPopulationSize ;

  public MOEADVariation(
      int offspringPopulationSize,
      MutationOperator<DoubleSolution> mutation) {
    this.mutation = mutation ;
    this.offspringPopulationSize = offspringPopulationSize ;

    this.matingPoolSize = offspringPopulationSize *
        crossover.getNumberOfRequiredParents() / crossover.getNumberOfGeneratedChildren();

    int remainder = matingPoolSize % crossover.getNumberOfRequiredParents();
    if (remainder != 0) {
      matingPoolSize += remainder;
    }
  }

  @Override
  public List<DoubleSolution> variate(List<DoubleSolution> solutionList, List<DoubleSolution> matingPopulation) {
    List<DoubleSolution> offspringPopulation = new ArrayList<>();
    for (int i = 0; i < solutionList.size(); i++) {
      crossover.setCurrentSolution(solutionList.get(i));
      List<DoubleSolution> parents = new ArrayList<>(3);
      for (int j = 0; j < 3; j++) {
        parents.add(matingPopulation.get(0));
        matingPopulation.remove(0);
      }

      List<DoubleSolution> offspring = crossover.execute(parents);

      offspringPopulation.add(mutation.execute(offspring.get(0)));
    }
    return offspringPopulation;
  }

  /**
   * A crossover operator is applied to a number of parents, and it assumed that the population contains
   * a valid number of population. This method checks that.
   *
   * @param population
   * @param numberOfParentsForCrossover
   */
  private void checkNumberOfParents(List<DoubleSolution> population, int numberOfParentsForCrossover) {
    if ((population.size() % numberOfParentsForCrossover) != 0) {
      throw new JMetalException("Wrong number of parents: the remainder if the " +
          "population size (" + population.size() + ") is not divisible by " +
          numberOfParentsForCrossover);
    }
  }

  @Override
  public int getMatingPoolSize() {
    return matingPoolSize ;
  }

  @Override
  public int getOffspringPopulationSize() {
    return offspringPopulationSize ;
  }
}
