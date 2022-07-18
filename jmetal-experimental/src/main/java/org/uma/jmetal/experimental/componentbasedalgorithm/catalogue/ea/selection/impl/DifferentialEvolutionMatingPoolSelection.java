package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.selection.impl;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.selection.MatingPoolSelection;
import org.uma.jmetal.operator.selection.impl.DifferentialEvolutionSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

public class DifferentialEvolutionMatingPoolSelection
    implements MatingPoolSelection<DoubleSolution> {
  private DifferentialEvolutionSelection selectionOperator;
  private int matingPoolSize;
  private SequenceGenerator<Integer> solutionIndexGenerator ;

  public DifferentialEvolutionMatingPoolSelection(
      int matingPoolSize, int numberOfParentsToSelect, boolean takeCurrentIndividualAsParent, SequenceGenerator<Integer> solutionIndexGenerator) {
    selectionOperator = new DifferentialEvolutionSelection(numberOfParentsToSelect, takeCurrentIndividualAsParent);
    this.matingPoolSize = matingPoolSize;
    this.solutionIndexGenerator = solutionIndexGenerator ;
  }

  public @NotNull List<DoubleSolution> select(List<DoubleSolution> solutionList) {
    List<DoubleSolution> matingPool = new ArrayList<>(matingPoolSize);

    while (matingPool.size() < matingPoolSize) {
      selectionOperator.setIndex(solutionIndexGenerator.getValue());
      @NotNull List<DoubleSolution> parents = selectionOperator.execute(solutionList) ;
      for (DoubleSolution parent: parents)  {
        matingPool.add(parent);
        if (matingPool.size() == matingPoolSize) {
          break ;
        }
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
