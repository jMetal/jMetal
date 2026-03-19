package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea.RVEAEnvironmentalSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * RVEA replacement strategy. It wraps the {@link RVEAEnvironmentalSelection} environmental selection.
 *
 * @param <S>
 */
public class RVEAReplacement<S extends Solution<?>> implements Replacement<S> {

  private final RVEAEnvironmentalSelection<S> rveaEnvironmentalSelection;

  public RVEAReplacement(RVEAEnvironmentalSelection<S> rveaEnvironmentalSelection) {
    this.rveaEnvironmentalSelection = rveaEnvironmentalSelection;
  }

  @Override
  public List<S> replace(List<S> currentList, List<S> offspringList) {
    Check.notNull(currentList);
    Check.notNull(offspringList);

    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(currentList);
    jointPopulation.addAll(offspringList);

    return rveaEnvironmentalSelection.execute(jointPopulation, currentList.size());
  }
}
