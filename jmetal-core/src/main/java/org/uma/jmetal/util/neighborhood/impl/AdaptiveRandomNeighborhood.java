package org.uma.jmetal.util.neighborhood.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the adaptive random neighborhood (topology) defined by M. Clerc.
 * Each {@link Solution} in a solution list must have a neighborhood composed by it itself and
 * K random selected neighbors (the same solution can be chosen several times).
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class AdaptiveRandomNeighborhood<S extends Solution<?>> implements Neighborhood<S> {
  private List<S> solutionList ;
  private int numberOfRandomNeighbours;
  private List<List<S>> neighbours;
  private JMetalRandom randomGenerator = JMetalRandom.getInstance() ;

  /**
   * Constructor
   * @param solutionList The list of solutions
   * @param numberOfRandomNeighbours The number of neighbors per solution
   */
  public AdaptiveRandomNeighborhood(List<S> solutionList, int numberOfRandomNeighbours) {
    if (solutionList.size() <= numberOfRandomNeighbours) {
      throw new JMetalException("The population size: " + solutionList.size() + " is" +
              "less or equal to the number of requested neighbors: "+ numberOfRandomNeighbours) ;
    }

    this.solutionList = solutionList ;
    this.numberOfRandomNeighbours = numberOfRandomNeighbours ;

    createNeighborhoods();
    addRandomNeighbors() ;
  }

  /**
   * Initialize all the neighborhoods, adding the current solution to them.
   */
  private void createNeighborhoods() {
    neighbours = new ArrayList<List<S>>(solutionList.size());

    for (int i = 0; i < neighbours.size(); i++) {
      neighbours.add(new ArrayList<S>());
      neighbours.get(i).add(solutionList.get(i));
    }
  }

  /**
   * Add random neighbors to all the neighborhoods
   */
  private void addRandomNeighbors() {
    for (int i = 0; i < solutionList.size(); i++) {
      while(neighbours.get(i).size() <= numberOfRandomNeighbours) {
        int random = randomGenerator.nextInt(0, solutionList.size() - 1);
        neighbours.get(i).add(solutionList.get(random)) ;
      }
    }
  }

  @Override
  public List<S> getNeighbors(List<S> solutionList, int solutionIndex) {
    if (solutionList == null) {
      throw new JMetalException("The solution list is null") ;
    } else if (solutionList.size() == 0) {
      throw new JMetalException("The solution list is empty") ;
    } else if (solutionIndex < 0) {
      throw new JMetalException("The solution position value is negative: " + solutionIndex);
    }
    else if (solutionIndex >= solutionList.size()) {
      throw new JMetalException("The solution position value " + solutionIndex +
              " is equal or greater than the solution list size "
              + solutionList.size()) ;
    }

    return neighbours.get(solutionIndex);
  }
}
