package org.uma.jmetal.component.catalogue.ea.variation.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.catalogue.ea.variation.Variation;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.Solution;

/**
 * A variation operator that applies only mutation (no crossover). Each mating pool member is
 * copied and mutated to produce one offspring.
 *
 * <p>Used by PAES, where offspring are always produced by mutating the current solution.
 *
 * @param <S> the solution type
 */
public class MutationOnlyVariation<S extends Solution<?>> implements Variation<S> {
  private final int offspringPopulationSize;
  private final MutationOperator<S> mutation;

  public MutationOnlyVariation(int offspringPopulationSize, MutationOperator<S> mutation) {
    this.offspringPopulationSize = offspringPopulationSize;
    this.mutation = mutation;
  }

  @Override
  public int matingPoolSize() {
    return offspringPopulationSize;
  }

  @Override
  public int offspringPopulationSize() {
    return offspringPopulationSize;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<S> variate(List<S> population, List<S> matingPool) {
    List<S> offspring = new ArrayList<>(offspringPopulationSize);
    for (S parent : matingPool) {
      S child = (S) parent.copy();
      mutation.execute(child);
      offspring.add(child);
    }
    return offspring;
  }
}
