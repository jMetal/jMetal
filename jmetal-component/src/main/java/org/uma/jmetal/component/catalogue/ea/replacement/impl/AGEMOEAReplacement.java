package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.agemoea.AGEMOEAEnvironmentalSelection;
import org.uma.jmetal.solution.Solution;

/**
 * Replacement component for AGE-MOEA and AGE-MOEA-II algorithms.
 * It uses the given {@link AGEMOEAEnvironmentalSelection} to select the survivors based on adaptive geometry estimation.
 *
 * @param <S>
 */
public class AGEMOEAReplacement<S extends Solution<?>> implements Replacement<S> {

  private AGEMOEAEnvironmentalSelection<S> environmentalSelection;

  public AGEMOEAReplacement(AGEMOEAEnvironmentalSelection<S> environmentalSelection) {
    this.environmentalSelection = environmentalSelection;
  }

  @Override
  public List<S> replace(List<S> population, List<S> offspringPopulation) {
    List<S> jointPopulation = new ArrayList<>(population.size() + offspringPopulation.size());
    jointPopulation.addAll(population);
    jointPopulation.addAll(offspringPopulation);

    List<S> result = environmentalSelection.execute(jointPopulation, population.size());
    
    return result;
  }
}
