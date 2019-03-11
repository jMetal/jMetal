package org.uma.jmetal.auto.selection.impl;

import org.uma.jmetal.auto.selection.MatingPoolSelection;
import org.uma.jmetal.operator.impl.selection.DifferentialEvolutionSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.List;

public class DifferentialEvolutionMatingPoolSelection implements MatingPoolSelection<DoubleSolution> {
  private DifferentialEvolutionSelection selectionOperator ;
  private int matingPoolSize ;

  public DifferentialEvolutionMatingPoolSelection(int matingPoolSize) {
    selectionOperator = new DifferentialEvolutionSelection();
    this.matingPoolSize = matingPoolSize ;
  }

  public List<DoubleSolution> select(List<DoubleSolution> solutionList) {
    List<DoubleSolution> matingPool = new ArrayList<>(matingPoolSize) ;

    int solutionCounter = 0 ;
    while (matingPool.size() < matingPoolSize) {
      selectionOperator.setIndex(solutionCounter);
      matingPool.addAll(selectionOperator.execute(solutionList)) ;
      solutionCounter++ ;
    }

    return matingPool ;
  }
}
