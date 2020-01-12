package org.uma.jmetal.auto.component.selection.impl;

import org.uma.jmetal.auto.component.selection.MatingPoolSelection;
import org.uma.jmetal.operator.selection.impl.DifferentialEvolutionSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

public class DifferentialEvolutionMatingPoolSelection implements MatingPoolSelection<DoubleSolution> {
  private DifferentialEvolutionSelection selectionOperator ;
  private int matingPoolSize ;
  private int solutionCounter ;

  public DifferentialEvolutionMatingPoolSelection(int matingPoolSize) {
    selectionOperator = new DifferentialEvolutionSelection();
    this.matingPoolSize = matingPoolSize ;
    this.solutionCounter = 0 ;
  }

  public List<DoubleSolution> select(List<DoubleSolution> solutionList) {
    List<DoubleSolution> matingPool = new ArrayList<>(matingPoolSize) ;

    while (matingPool.size() < matingPoolSize) {
      selectionOperator.setIndex(solutionCounter);
      matingPool.addAll(selectionOperator.execute(solutionList)) ;
      solutionCounter++ ;
      if (solutionCounter % solutionList.size() == 0) {
        solutionCounter = 0 ;
      }
    }

    return matingPool ;
  }
}
