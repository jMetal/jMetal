package org.uma.jmetal.auto.component.selection.impl;

import org.uma.jmetal.auto.component.selection.MatingPoolSelection;
import org.uma.jmetal.auto.util.checking.Checker;
import org.uma.jmetal.operator.selection.impl.RandomSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class RandomMatingPoolSelection<S extends Solution<?>> implements MatingPoolSelection<S> {
  private int matingPoolSize;

  public RandomMatingPoolSelection(int matingPoolSize) {
    this.matingPoolSize = matingPoolSize;
  }

  public List<S> select(List<S> solutionList) {
    Checker.isNotNull(solutionList);
    /*
        Checker.that(
            solutionList.size() >= matingPoolSize,
            "The solution list size ("
                + solutionList.size()
                + ") is lower than the mating pool size ("
                + matingPoolSize
                + ")");

        List<S> matingPool =
            SolutionListUtils.selectNRandomDifferentSolutions(matingPoolSize, solutionList);
    */
    List<S> matingPool = new ArrayList<>();
    IntStream.range(0, matingPoolSize)
        .forEach(
            i ->
                matingPool.add(
                    solutionList.get(JMetalRandom.getInstance().nextInt(0, solutionList.size()-1))));

    return matingPool;
  }
}
