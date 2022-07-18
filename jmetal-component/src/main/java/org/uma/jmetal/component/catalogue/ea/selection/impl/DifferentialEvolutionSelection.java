package org.uma.jmetal.component.catalogue.ea.selection.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.component.catalogue.ea.selection.Selection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;


public class DifferentialEvolutionSelection
    implements Selection<DoubleSolution> {
  private org.uma.jmetal.operator.selection.impl.DifferentialEvolutionSelection selectionOperator;
  private int matingPoolSize;
  private SequenceGenerator<Integer> solutionIndexGenerator ;

  public DifferentialEvolutionSelection(
      int matingPoolSize, int numberOfParentsToSelect, boolean takeCurrentIndividualAsParent, SequenceGenerator<Integer> solutionIndexGenerator) {
    selectionOperator = new org.uma.jmetal.operator.selection.impl.DifferentialEvolutionSelection(numberOfParentsToSelect, takeCurrentIndividualAsParent);
    this.matingPoolSize = matingPoolSize;
    this.solutionIndexGenerator = solutionIndexGenerator ;
  }

  public List<DoubleSolution> select(List<DoubleSolution> solutionList) {
    List<DoubleSolution> matingPool = new ArrayList<>(matingPoolSize);

    while (matingPool.size() < matingPoolSize) {
      selectionOperator.setIndex(solutionIndexGenerator.getValue());
      var parents = selectionOperator.execute(solutionList) ;
      for (var parent: parents)  {
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
