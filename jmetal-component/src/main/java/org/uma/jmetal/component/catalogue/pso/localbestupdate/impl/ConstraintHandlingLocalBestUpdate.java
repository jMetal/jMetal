package org.uma.jmetal.component.catalogue.pso.localbestupdate.impl;

import java.util.List;
import java.util.function.DoubleSupplier;
import org.uma.jmetal.component.catalogue.pso.localbestupdate.LocalBestUpdate;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.constrainthandling.PairwiseConstraintHandlingTechnique;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * A local best update with a pairwise constraint-handling technique: once per iteration it
 * updates the technique with the swarm and the progress of the run, and a particle replaces its
 * local best unless the technique prefers the local best. The global leaders, kept in the archive
 * of the algorithm, are compared with the constrained-domination principle whatever the technique.
 */
public class ConstraintHandlingLocalBestUpdate implements LocalBestUpdate {
  private final PairwiseConstraintHandlingTechnique<DoubleSolution> technique;
  private final DoubleSupplier progress;

  /**
   * Creates the local best update.
   *
   * @param technique the constraint-handling technique
   * @param progress the progress of the run
   */
  public ConstraintHandlingLocalBestUpdate(
      PairwiseConstraintHandlingTechnique<DoubleSolution> technique, DoubleSupplier progress) {
    Check.notNull(technique);
    Check.notNull(progress);
    this.technique = technique;
    this.progress = progress;
  }

  @Override
  public DoubleSolution[] update(List<DoubleSolution> swarm, DoubleSolution[] localBest) {
    technique.update(swarm, progress.getAsDouble());
    var comparator = technique.comparator();
    for (int i = 0; i < swarm.size(); i++) {
      if (comparator.compare(swarm.get(i), localBest[i]) <= 0) {
        localBest[i] = (DoubleSolution) swarm.get(i).copy();
      }
    }
    return localBest;
  }

  /** The constraint-handling technique. */
  public PairwiseConstraintHandlingTechnique<DoubleSolution> technique() {
    return technique;
  }
}
