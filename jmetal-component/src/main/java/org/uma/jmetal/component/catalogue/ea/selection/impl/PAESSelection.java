package org.uma.jmetal.component.catalogue.ea.selection.impl;

import java.util.List;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Selection operator for PAES. Each parent is chosen as either the current solution (the single
 * member of the working population) or a randomly selected member of the PAES archive, according
 * to {@code archiveSelectionProbability}.
 *
 * <p>This generalizes classic PAES: with a probability of 0.0 the current solution is always
 * mutated (standard behaviour); higher values introduce archive-based parent selection, analogous
 * to MOEA/D's {@code neighborhoodSelectionProbability}.
 *
 * <p>When the archive is still empty (before the first solution is stored), the current solution is
 * always selected.
 *
 * @param <S> the solution type
 */
public class PAESSelection<S extends Solution<?>> implements Selection<S> {
  private final double archiveSelectionProbability;
  private final BoundedArchive<S> archive;
  private final JMetalRandom random = JMetalRandom.getInstance();

  public PAESSelection(double archiveSelectionProbability, BoundedArchive<S> archive) {
    this.archiveSelectionProbability = archiveSelectionProbability;
    this.archive = archive;
  }

  @Override
  public List<S> select(List<S> solutionList) {
    List<S> archiveSolutions = archive.solutions();
    S selected =
        (!archiveSolutions.isEmpty() && random.nextDouble() < archiveSelectionProbability)
            ? archiveSolutions.get(random.nextInt(0, archiveSolutions.size() - 1))
            : solutionList.getFirst();
    return List.of(selected);
  }
}
