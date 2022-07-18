package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.selection.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.selection.MatingPoolSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class RandomMatingPoolSelection<S extends Solution<?>> implements MatingPoolSelection<S> {
  private int matingPoolSize;

  public RandomMatingPoolSelection(int matingPoolSize) {
    this.matingPoolSize = matingPoolSize;
  }

  public List<S> select(List<S> solutionList) {
    Check.notNull(solutionList);

    List<S> matingPool = new ArrayList<>();
      int bound = matingPoolSize;
      for (int i = 0; i < bound; i++) {
          matingPool.add(
                  solutionList.get(JMetalRandom.getInstance().nextInt(0, solutionList.size() - 1)));
      }

      return matingPool;
  }
}
