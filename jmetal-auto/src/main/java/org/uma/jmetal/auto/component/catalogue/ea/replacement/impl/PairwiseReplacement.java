package org.uma.jmetal.auto.component.catalogue.ea.replacement.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.auto.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Given two populations of equal size, the returned population is composed of the result of the
 * pairwise comparison between the solutions of both populations.
 *
 * @param <S>
 */
public class PairwiseReplacement<S extends Solution<?>> implements Replacement<S> {
  protected Comparator<S> comparator;

  public PairwiseReplacement(Comparator<S> comparator) {
    this.comparator = comparator;
  }

  public List<S> replace(List<S> population, List<S> offspringPopulation) {
    Check.that(
        population.size() == offspringPopulation.size(),
        "The sizes of both populations is not the same: "
            + population.size()
            + ", "
            + offspringPopulation.size());

    List<S> resultPopulation = new ArrayList<>();
    for (int i = 0; i < population.size(); i++) {
      if (comparator.compare(population.get(i), offspringPopulation.get(i)) < 0) {
        resultPopulation.add(population.get(i)) ;
      } else {
        resultPopulation.add(offspringPopulation.get(i)) ;
      }
    }

    return resultPopulation;
  }
}
