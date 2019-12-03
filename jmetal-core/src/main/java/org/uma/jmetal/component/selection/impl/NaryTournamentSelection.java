package org.uma.jmetal.component.selection.impl;

import org.uma.jmetal.component.selection.Selection;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NaryTournamentSelection<S extends Solution<?>> implements Selection<S> {
  private org.uma.jmetal.operator.selection.impl.NaryTournamentSelection<S> selectionOperator;
  private int matingPoolSize;

  @Deprecated
  public NaryTournamentSelection(int tournamentSize, int matingPoolSize, Comparator<S> comparator) {
    selectionOperator =
        new org.uma.jmetal.operator.selection.impl.NaryTournamentSelection<>(
            tournamentSize, comparator);
    this.matingPoolSize = matingPoolSize;
  }

  public List<S> select(List<S> solutionList) {
    List<S> matingPool = new ArrayList<>(matingPoolSize);

    while (matingPool.size() < matingPoolSize) {
      matingPool.add(selectionOperator.execute(solutionList));
    }

    return matingPool;
  }
}
