package org.uma.jmetal.component.replacement.impl;

import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.checking.Check;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * (mu , lambda) replacement. Given a population of size mu and an offspring population of size
 * lambda, both are merged and the population with the best mu solutions (according to a comparator)
 * from the offspring population are returned. The condition mu < lambda must hold.
 *
 * @param <S>
 */
public class MuCommaLambdaReplacement<S extends Solution<?>> implements Replacement<S> {
  protected Comparator<S> comparator;

  public MuCommaLambdaReplacement(Comparator<S> comparator) {
    this.comparator = comparator;
  }

  public List<S> replace(List<S> population, List<S> offspringPopulation) {
    Check.that(
        population.size() < offspringPopulation.size(),
        "Mu ("
            + population.size()
            + ") must be lower than lambda ("
            + offspringPopulation.size()
            + ")");

    List<S> jointPopulation = new ArrayList<>(offspringPopulation);

    jointPopulation.sort(comparator);

    while (jointPopulation.size() > population.size()) {
      jointPopulation.remove(jointPopulation.size() - 1);
    }

    return jointPopulation;
  }
}
