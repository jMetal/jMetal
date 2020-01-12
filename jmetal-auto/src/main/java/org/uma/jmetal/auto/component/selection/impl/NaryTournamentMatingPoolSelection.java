package org.uma.jmetal.auto.component.selection.impl;

import org.uma.jmetal.auto.component.selection.MatingPoolSelection;
import org.uma.jmetal.auto.util.preference.Preference;
import org.uma.jmetal.operator.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.MultiComparator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class NaryTournamentMatingPoolSelection<S extends Solution<?>>
    implements MatingPoolSelection<S> {
  private NaryTournamentSelection<S> selectionOperator;
  private int matingPoolSize;
  private Preference<S> preference ;

  @Deprecated
  public NaryTournamentMatingPoolSelection(
      int tournamentSize, int matingPoolSize, Comparator<S> comparator) {
    selectionOperator = new NaryTournamentSelection<>(tournamentSize, comparator);
    this.matingPoolSize = matingPoolSize;
  }

  public NaryTournamentMatingPoolSelection(
          int tournamentSize, int matingPoolSize, Preference<S> preference) {
    this.preference = preference ;
    Comparator<S> comparator = new MultiComparator<>(
            Arrays.asList(
                    preference.getRanking().getSolutionComparator(),
                    preference.getDensityEstimator().getSolutionComparator())) ;
    this.selectionOperator = new NaryTournamentSelection<>(tournamentSize, comparator) ;
    this.matingPoolSize = matingPoolSize ;
  }

  public List<S> select(List<S> solutionList) {
    preference.recompute(solutionList);
    List<S> matingPool = new ArrayList<>(matingPoolSize);

    while (matingPool.size() < matingPoolSize) {
      matingPool.add(selectionOperator.execute(solutionList));
    }

    return matingPool;
  }


}
