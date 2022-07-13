package org.uma.jmetal.component.catalogue.ea.selection.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.component.util.RankingAndDensityEstimatorPreference;
import org.uma.jmetal.solution.Solution;

public class NaryTournamentSelection<S extends Solution<?>>
    implements Selection<S> {
  private org.uma.jmetal.operator.selection.impl.NaryTournamentSelection<S> selectionOperator;
  private int matingPoolSize;
  private RankingAndDensityEstimatorPreference<S> preference;

  public NaryTournamentSelection(
      org.uma.jmetal.operator.selection.impl.NaryTournamentSelection<S> selection, int matingPoolSize) {
    this.matingPoolSize = matingPoolSize ;
    this.selectionOperator = selection ;
  }

  public NaryTournamentSelection(
      int tournamentSize, int matingPoolSize, Comparator<S> comparator) {
    selectionOperator = new org.uma.jmetal.operator.selection.impl.NaryTournamentSelection<>(tournamentSize, comparator);
    this.matingPoolSize = matingPoolSize;
    preference = null ;
  }

  public NaryTournamentSelection(
      int tournamentSize, int matingPoolSize, RankingAndDensityEstimatorPreference<S> preference) {
    this.preference = preference ;

    this.selectionOperator = new org.uma.jmetal.operator.selection.impl.NaryTournamentSelection<>(tournamentSize, preference.getComparator());
    this.matingPoolSize = matingPoolSize;
  }

  public List<S> select(List<S> solutionList) {
    if (null != preference) {
      preference.recompute(solutionList) ;
    }
    List<S> matingPool = new ArrayList<>(matingPoolSize);

    while (matingPool.size() < matingPoolSize) {
      matingPool.add(selectionOperator.execute(solutionList));
    }

    return matingPool;
  }
}
