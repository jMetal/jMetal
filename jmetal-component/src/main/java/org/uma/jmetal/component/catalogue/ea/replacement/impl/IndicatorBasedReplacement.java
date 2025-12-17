package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.util.IndicatorBasedPreference;
import org.uma.jmetal.solution.Solution;

/**
 * Indicator-based replacement strategy following the IBEA approach. This strategy uses
 * hypervolume-based fitness values to select which solutions survive to the next generation.
 *
 * <p>The replacement works by:
 *
 * <ol>
 *   <li>Combining parent and offspring populations
 *   <li>Computing indicator-based fitness for all solutions
 *   <li>Iteratively removing the worst solution (highest fitness) until the desired population size
 *       is reached
 *   <li>Updating fitness values after each removal to maintain accuracy
 * </ol>
 *
 * <p>This strategy is particularly effective for multi-objective optimization as it directly
 * optimizes the hypervolume indicator, which is known to produce well-distributed Pareto fronts.
 *
 * <p>The {@link IndicatorBasedPreference} can be shared between selection and replacement
 * components to ensure consistent fitness-based comparisons throughout the algorithm.
 *
 * <p>Control parameters (via {@link IndicatorBasedPreference}):
 *
 * <ul>
 *   <li><b>kappa</b>: Scaling factor for fitness calculation. Range: (0, +∞). Default: 0.05.
 *       Smaller values increase selection pressure toward better solutions.
 * </ul>
 *
 * @author Antonio J. Nebro
 * @param <S> Type of the solutions
 * @see IndicatorBasedPreference
 */
public class IndicatorBasedReplacement<S extends Solution<?>> implements Replacement<S> {

  private final IndicatorBasedPreference<S> preference;

  /**
   * Constructor with an indicator-based preference.
   *
   * @param preference The indicator-based preference for computing fitness values
   */
  public IndicatorBasedReplacement(IndicatorBasedPreference<S> preference) {
    this.preference = preference;
  }


  /**
   * Replaces the current population by selecting solutions from the combined parent and offspring
   * populations using indicator-based fitness.
   *
   * <p>The method iteratively removes the worst solution (highest fitness value) until the
   * population size equals the original parent population size. After each removal, fitness values
   * are updated to reflect the new population composition.
   *
   * @param population Current population of size N
   * @param offspringPopulation Offspring population
   * @return New population of size N with the best solutions according to indicator-based fitness
   */
  @Override
  public List<S> replace(List<S> population, List<S> offspringPopulation) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(population);
    jointPopulation.addAll(offspringPopulation);

    // Compute indicator-based fitness for all solutions
    preference.compute(jointPopulation);

    // Iteratively remove worst solutions until we reach the desired size
    while (jointPopulation.size() > population.size()) {
      preference.removeWorstAndUpdateFitness(jointPopulation);
    }

    return jointPopulation;
  }

  /**
   * Gets the indicator-based preference used by this replacement.
   *
   * @return The indicator-based preference
   */
  public IndicatorBasedPreference<S> getPreference() {
    return preference;
  }
}
