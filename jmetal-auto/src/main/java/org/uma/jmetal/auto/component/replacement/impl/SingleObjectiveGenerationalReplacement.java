package org.uma.jmetal.auto.component.replacement.impl;

import org.uma.jmetal.auto.component.replacement.Replacement;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SingleObjectiveGenerationalReplacement<S extends Solution<?>> implements Replacement<S> {
  private DominanceComparator<S> dominanceComparator = new DominanceComparator<>() ;

  public List<S> replace(List<S> currentList, List<S> offspringList) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(currentList);
    jointPopulation.addAll(offspringList);

    Collections.sort(jointPopulation, new ObjectiveComparator<S>(0));

    while (jointPopulation.size() > currentList.size()) {
      jointPopulation.remove(jointPopulation.size() - 1);
    }

    return jointPopulation;
  }
}
