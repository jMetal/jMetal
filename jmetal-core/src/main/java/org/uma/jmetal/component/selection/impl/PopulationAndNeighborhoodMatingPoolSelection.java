package org.uma.jmetal.component.selection.impl;

import org.uma.jmetal.component.selection.MatingPoolSelection;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.NaryRandomSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;

import java.util.List;

/**
 * This class allows to select N different solutions that can be taken from a solution list (i.e, population or swarm) or
 * from a neighborhood according to a given probability.
 *
 * @author Antonio J. Nebro

 * @param <S> Type of the solutions
 */
public class PopulationAndNeighborhoodMatingPoolSelection<S extends Solution<?>>
    implements MatingPoolSelection<S> {
  private SelectionOperator<List<S>, List<S>> selectionOperator;
  private int matingPoolSize;

  private SequenceGenerator<Integer> solutionIndexGenerator;
  private Neighborhood<S> neighborhood;

  private Neighborhood.NeighborType neighborType;
  private double neighborhoodSelectionProbability;

  private boolean selectCurrentSolution ;

  public PopulationAndNeighborhoodMatingPoolSelection(
      int matingPoolSize,
      SequenceGenerator<Integer> solutionIndexGenerator,
      Neighborhood<S> neighborhood,
      double neighborhoodSelectionProbability,
      boolean selectCurrentSolution) {
    this.matingPoolSize = matingPoolSize;
    this.solutionIndexGenerator = solutionIndexGenerator;
    this.neighborhood = neighborhood;
    this.neighborhoodSelectionProbability = neighborhoodSelectionProbability;
    this.selectCurrentSolution = selectCurrentSolution ;

    selectionOperator = new NaryRandomSelection<S>(selectCurrentSolution ? matingPoolSize - 1 : matingPoolSize);
  }

  public List<S> select(List<S> solutionList) {
    List<S> matingPool;

    if (JMetalRandom.getInstance().nextDouble() < neighborhoodSelectionProbability) {
      neighborType = Neighborhood.NeighborType.NEIGHBOR;
      matingPool =
          selectionOperator.execute(
              neighborhood.getNeighbors(solutionList, solutionIndexGenerator.getValue()));
    } else {
      neighborType = Neighborhood.NeighborType.POPULATION;
      matingPool = selectionOperator.execute(solutionList);
    }

    if (selectCurrentSolution) {
      matingPool.add(solutionList.get(solutionIndexGenerator.getValue())) ;
    }

    Check.that(
        matingPoolSize == matingPool.size(),
        "The mating pool size "
            + matingPool.size()
            + " is not equal to the required size "
            + matingPoolSize);

    return matingPool;
  }

  public Neighborhood.NeighborType getNeighborType() {
    return neighborType;
  }

  public SequenceGenerator<Integer> getSolutionIndexGenerator() {
    return solutionIndexGenerator;
  }
}
