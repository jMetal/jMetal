package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.solution.Solution;

/**
 * (mu + lambda) replacement. Given a population of size mu and an offspring population of size lambda,
 * both are merged and the population with the best mu solutions (according to a comparator) are returned.
 *
 * @param <S>
 */
public class MuPlusLambdaReplacement<S extends Solution<?>> implements Replacement<S> {
  protected Comparator<S> comparator ;

  public MuPlusLambdaReplacement(Comparator<S> comparator) {
    this.comparator = comparator ;
  }

  public @NotNull List<S> replace(@NotNull List<S> population, List<S> offspringPopulation) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population);
    jointPopulation.addAll(offspringPopulation);

    jointPopulation.sort(comparator);

    while (jointPopulation.size() > population.size()) {
      jointPopulation.remove(jointPopulation.size() - 1);
    }

    return jointPopulation;
  }
}
