package org.uma.jmetal.component.replacement.impl;

import org.uma.jmetal.component.replacement.Replacement;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Given a population of size N and an offspring population of size M (N could be equal to M),
 * both are merged and the a population with the best N solutions (according to a comparator)
 * are returned.
 *
 * @param <S>
 */
public class MergeReplacementReplacement<S extends Solution<?>> implements Replacement<S> {
  protected Comparator<S> comparator ;

  public MergeReplacementReplacement(Comparator<S> comparator) {
    this.comparator = comparator ;
  }

  public List<S> replace(List<S> population, List<S> offspringPopulation) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population);
    jointPopulation.addAll(offspringPopulation);

    jointPopulation.sort(comparator);
    //jointPopulation.sort(new ObjectiveComparator<S>(0));

    while (jointPopulation.size() > population.size()) {
      jointPopulation.remove(jointPopulation.size() - 1);
    }

    return jointPopulation;
  }
}
