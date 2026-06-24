package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.BoundedArchive;

/**
 * Replacement strategy for PAES (Pareto Archived Evolution Strategy).
 *
 * <p>Three-way logic on each call:
 * <ol>
 *   <li>Offspring dominates current → accept offspring, add to archive.</li>
 *   <li>Current dominates offspring → keep current (no change).</li>
 *   <li>Neither dominates → try adding offspring to archive; if accepted, use the archive's
 *       density comparator to choose between current and offspring.</li>
 * </ol>
 *
 * <p>The current solution is added to the archive on every call so that the initial solution is
 * tracked from the first iteration without requiring a separate initialization step.
 *
 * @param <S> the solution type
 */
public class PAESReplacement<S extends Solution<?>> implements Replacement<S> {
  private final BoundedArchive<S> paesArchive;
  private final Comparator<S> dominanceComparator;

  public PAESReplacement(BoundedArchive<S> paesArchive, Comparator<S> dominanceComparator) {
    this.paesArchive = paesArchive;
    this.dominanceComparator = dominanceComparator;
  }

  @Override
  public List<S> replace(List<S> population, List<S> offspringPopulation) {
    S current = population.get(0);
    S offspring = offspringPopulation.get(0);

    // Ensure current is tracked (handles the initial solution on the first call; no-op thereafter)
    paesArchive.add(current);

    int flag = dominanceComparator.compare(current, offspring);
    if (flag > 0) {
      // offspring dominates current
      paesArchive.add(offspring);
      population.set(0, offspring);
    } else if (flag == 0) {
      // Neither dominates the other: use the archive density as a tiebreaker, but only once the
      // archive is full. While it is still filling up the current solution is always kept, which
      // provides the convergence pressure of the (1+1) strategy: density-based moves only start
      // after the archive is full (this mirrors classic PAES, where BoundedArchive.prune() refreshes
      // the density only on overflow). Computing density on every call instead makes the search
      // drift before it converges (severely so with the continuous crowding-distance estimator).
      // The density is recomputed explicitly here so that every estimator type has its attributes
      // set before the comparator reads them (some estimators, e.g. angle or spatial spread, have no
      // default value and would otherwise fail on a missing attribute).
      if (paesArchive.add(offspring)
          && paesArchive.solutions().size() >= paesArchive.maximumSize()) {
        paesArchive.computeDensityEstimator();
        if (paesArchive.comparator().compare(current, offspring) > 0) {
          population.set(0, offspring);
        }
      }
    }
    // flag < 0: current dominates offspring → no change
    return population;
  }
}
