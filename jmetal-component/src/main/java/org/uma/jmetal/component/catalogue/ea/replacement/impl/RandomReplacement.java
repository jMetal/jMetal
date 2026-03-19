package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ListUtils;

/**
 * Random replacement strategy that randomly selects N solutions from the combined
 * parent and offspring populations, where N is the size of the parent population.
 *
 * <p>This strategy provides zero selection pressure and is useful as a baseline
 * or null hypothesis in automatic algorithm configuration experiments.
 *
 * <p>Control parameters: None. This operator has no tunable parameters.
 *
 * @author Antonio J. Nebro
 * @param <S> Type of the solutions
 */
public class RandomReplacement<S extends Solution<?>> implements Replacement<S> {

  /**
   * Replaces the current population by randomly selecting solutions from the combined
   * parent and offspring populations.
   *
   * @param population Current population of size N
   * @param offspringPopulation Offspring population
   * @return New population of size N randomly selected from combined population
   */
  @Override
  public List<S> replace(List<S> population, List<S> offspringPopulation) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population);
    jointPopulation.addAll(offspringPopulation);

    return ListUtils.randomSelectionWithoutReplacement(population.size(), jointPopulation);
  }
}
