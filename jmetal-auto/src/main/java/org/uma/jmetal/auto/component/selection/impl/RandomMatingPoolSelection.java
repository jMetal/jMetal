package org.uma.jmetal.auto.component.selection.impl;

import org.uma.jmetal.auto.component.selection.MatingPoolSelection;
import org.uma.jmetal.auto.util.checking.Checker;
import org.uma.jmetal.operator.selection.impl.RandomSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;

import java.util.List;

public class RandomMatingPoolSelection<S extends Solution<?>> implements MatingPoolSelection<S> {
  private int matingPoolSize;

  public RandomMatingPoolSelection(int matingPoolSize) {
    this.matingPoolSize = matingPoolSize;
  }

  public List<S> select(List<S> solutionList) {
    Checker.isNotNull(solutionList);
    Checker.isTrue(
        solutionList.size() >= matingPoolSize,
        "The solution list size is lower than the mating pool size");

    List<S> matingPool =
        SolutionListUtils.selectNRandomDifferentSolutions(matingPoolSize, solutionList);

    return matingPool;
  }
}
