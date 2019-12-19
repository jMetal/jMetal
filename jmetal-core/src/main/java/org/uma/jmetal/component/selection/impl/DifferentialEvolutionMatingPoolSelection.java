package org.uma.jmetal.component.selection.impl;

import org.uma.jmetal.component.selection.MatingPoolSelection;
import org.uma.jmetal.operator.selection.impl.DifferentialEvolutionSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.checking.Check;

import java.util.ArrayList;
import java.util.List;

public class DifferentialEvolutionMatingPoolSelection
    implements MatingPoolSelection<DoubleSolution> {
  private DifferentialEvolutionSelection selectionOperator;
  private int matingPoolSize;
  private int solutionCounter;

  public DifferentialEvolutionMatingPoolSelection(
      int matingPoolSize, int numberOfParentsToSelect, boolean takeCurrentIndividualAsParent) {
    selectionOperator = new DifferentialEvolutionSelection(numberOfParentsToSelect, takeCurrentIndividualAsParent);
    this.matingPoolSize = matingPoolSize;
    this.solutionCounter = 0;
  }

  public List<DoubleSolution> select(List<DoubleSolution> solutionList) {
    List<DoubleSolution> matingPool = new ArrayList<>(matingPoolSize);

    while (matingPool.size() < matingPoolSize) {
      selectionOperator.setIndex(solutionCounter);
      List<DoubleSolution> parents = selectionOperator.execute(solutionList) ;
      for (DoubleSolution parent: parents)  {
        matingPool.add(parent);
        if (matingPool.size() == matingPoolSize) {
          break ;
        }
      }
      solutionCounter++;
      if (solutionCounter % solutionList.size() == 0) {
        solutionCounter = 0;
      }
    }

    Check.that(
        matingPoolSize == matingPool.size(),
        "The mating pool size "
            + matingPool.size()
            + " is not equal to the required size "
            + matingPoolSize);

    return matingPool;
  }
}
