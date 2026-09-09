package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.rvea.RVEAEnvironmentalSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;

/** Joins parents and offspring before invoking a stateful RVEA-family environmental selection. */
public class RVEAReplacement<S extends Solution<?>> implements Replacement<S> {
  private final RVEAEnvironmentalSelection<S> environmentalSelection;

  public RVEAReplacement(RVEAEnvironmentalSelection<S> environmentalSelection) {
    Check.notNull(environmentalSelection);
    this.environmentalSelection = environmentalSelection;
  }

  @Override
  public List<S> replace(List<S> parents, List<S> offspring) {
    Check.notNull(parents);
    Check.notNull(offspring);
    List<S> joint = new ArrayList<>(parents.size() + offspring.size());
    joint.addAll(parents);
    joint.addAll(offspring);
    return environmentalSelection.execute(joint, environmentalSelection.populationSize());
  }
}
