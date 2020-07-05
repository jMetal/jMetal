package org.uma.jmetal.component.variation.impl;

import org.uma.jmetal.component.variation.Variation;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.NullMutation;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

import java.util.ArrayList;
import java.util.List;

/** */
public class DifferentialCrossoverVariation<S extends Solution<Double>> implements Variation<S> {
  private int matingPoolSize;
  private int offspringPopulationSize;

  private SequenceGenerator<Integer> solutionIndexGenerator ;

  private DifferentialEvolutionCrossover<S> crossover;

  private MutationOperator<S> mutation;

  public DifferentialCrossoverVariation(
      int offspringPopulationSize,
      DifferentialEvolutionCrossover<S> crossover,
      MutationOperator<S> mutation, SequenceGenerator<Integer> solutionIndexGenerator) {
    this.offspringPopulationSize = offspringPopulationSize;
    this.crossover = crossover;
    this.mutation = mutation;
    this.solutionIndexGenerator = solutionIndexGenerator ;

    this.matingPoolSize = offspringPopulationSize * crossover.getNumberOfRequiredParents();
  }

  public DifferentialCrossoverVariation(
      int offspringPopulationSize, DifferentialEvolutionCrossover<S> crossover, SequenceGenerator<Integer> solutionIndexGenerator) {
    this(offspringPopulationSize, crossover, new NullMutation<>(), solutionIndexGenerator);
  }

  @Override
  public List<S> variate(
      List<S> solutionList, List<S> matingPool) {

    List<S> offspringPopulation = new ArrayList<>();
    while (offspringPopulation.size() < offspringPopulationSize) {
      crossover.setCurrentSolution(solutionList.get(solutionIndexGenerator.getValue()));

      int numberOfRequiredParentsToCross = crossover.getNumberOfRequiredParents() ;

      List<S> parents = new ArrayList<>(numberOfRequiredParentsToCross);
      for (int j = 0; j < numberOfRequiredParentsToCross; j++) {
        parents.add(matingPool.get(0));
        matingPool.remove(0);
      }

      List<S> offspring = crossover.execute(parents);

      offspringPopulation.add(mutation.execute(offspring.get(0)));
    }

    return offspringPopulation;
  }

  public DifferentialEvolutionCrossover<S> getCrossover() {
    return crossover;
  }

  public MutationOperator<S> getMutation() {
    return mutation;
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
