package org.uma.jmetal.auto.selection.impl;

import org.uma.jmetal.auto.selection.MatingPoolSelection;
import org.uma.jmetal.auto.util.checking.Checker;
import org.uma.jmetal.operator.impl.selection.NaryTournamentSelection;
import org.uma.jmetal.operator.impl.selection.RandomSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RandomMatingPoolSelection<S extends Solution<?>> implements MatingPoolSelection<S> {
  private RandomSelection<S> selectionOperator;
  private int matingPoolSize;
  private Checker check = new Checker();

  public RandomMatingPoolSelection(int matingPoolSize) {
    selectionOperator = new RandomSelection<>();
    this.matingPoolSize = matingPoolSize;
  }

  public List<S> select(List<S> solutionList) {
    check
        .isNotNull(solutionList)
        .isTrue(
            solutionList.size() >= matingPoolSize,
            "The solution list size is lower than the mating pool size");

    List<S> matingPool = SolutionListUtils.selectNRandomDifferentSolutions(matingPoolSize, solutionList);

    return matingPool;
  }
}
