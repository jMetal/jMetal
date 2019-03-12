package org.uma.jmetal.auto.variation.impl;

import org.uma.jmetal.auto.variation.Variation;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class DifferentialCrossoverVariation implements Variation<DoubleSolution> {
  private int matingPoolSize ;
  private int offspringPopulationSize ;

  private DifferentialEvolutionCrossover crossover;

  public DifferentialCrossoverVariation(int offspringPopulationSize, DifferentialEvolutionCrossover crossover) {
    this.offspringPopulationSize = offspringPopulationSize ;
    this.crossover = crossover ;

    this.matingPoolSize = offspringPopulationSize * 3 ;
  }

  @Override
  public List<DoubleSolution> variate(List<DoubleSolution> solutionList, List<DoubleSolution> matingPool) {

    List<DoubleSolution> offspringPopulation = new ArrayList<>();
    for (int i = 0; i < solutionList.size(); i ++) {
      crossover.setCurrentSolution(solutionList.get(i));
      List<DoubleSolution> parents = new ArrayList<>(3);
      for (int j = 0; j < 3; j++) {
        parents.add(matingPool.get(0));
        matingPool.remove(0);
      }

      List<DoubleSolution> offspring = crossover.execute(parents);

      offspringPopulation.add(offspring.get(0));
    }
    return offspringPopulation;
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
