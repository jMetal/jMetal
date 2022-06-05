package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.selection.impl;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.selection.MatingPoolSelection;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

/**
 * This class produces a mating pool composed of solutions belonging to a neighborhood. The neighborhood is associated
 * to a particular solution, which is determined by its position in the population as indicated by a
 * {@link SequenceGenerator} object.
 *
 * @author Antonio J. Nebro
 * @param <S> Type of the solutions
 */
public class NeighborhoodMatingPoolSelection<S extends Solution<?>>
    implements MatingPoolSelection<S> {
  private SelectionOperator<List<S>, S> selectionOperator;
  private int matingPoolSize;
  private boolean updateCurrentSolutionIndex ;

  private SequenceGenerator<Integer> solutionIndexGenerator;
  private Neighborhood<S> neighborhood;

  public NeighborhoodMatingPoolSelection(
      int matingPoolSize,
      SequenceGenerator<Integer> solutionIndexGenerator,
      Neighborhood<S> neighborhood,
      SelectionOperator<List<S>, S> selectionOperator, boolean updateCurrentSolutionIndex) {
    this.matingPoolSize = matingPoolSize;
    this.solutionIndexGenerator = solutionIndexGenerator;
    this.neighborhood = neighborhood;
    this.selectionOperator = selectionOperator ;
    this.updateCurrentSolutionIndex = updateCurrentSolutionIndex ;
  }

  public List<S> select(List<S> solutionList) {
    List<S> matingPool = new ArrayList<>();

    while (matingPool.size() < matingPoolSize) {
      matingPool.add(
          selectionOperator.execute(
              neighborhood.getNeighbors(solutionList, solutionIndexGenerator.getValue())));

      if (updateCurrentSolutionIndex) {
        solutionIndexGenerator.generateNext();
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

  public SequenceGenerator<Integer> getSolutionIndexGenerator() {
    return solutionIndexGenerator;
  }
}
