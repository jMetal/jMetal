package org.uma.jmetal.auto.component.selection.impl;

import org.uma.jmetal.auto.component.selection.MatingPoolSelection;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.neighborhood.Neighborhood;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.List;

public class MOEADSelection implements MatingPoolSelection<DoubleSolution> {
  private int matingPoolSize ;
  private Neighborhood<DoubleSolution> neighborhood ;
  private double neighborhoodSelectionProbability ;
  private int currentSolutionIndex ;
  private String neighbourType ;

  public MOEADSelection(int matingPoolSize, Neighborhood<DoubleSolution> neighborhood,
                        double neighborhoodSelectionProbability) {
    this.matingPoolSize = matingPoolSize ;
    this.neighborhood = neighborhood ;
    this.neighborhoodSelectionProbability = neighborhoodSelectionProbability ;
    currentSolutionIndex = 0 ;
  }

  public List<DoubleSolution> select(List<DoubleSolution> solutionList) {
    if (JMetalRandom.getInstance().nextDouble() < neighborhoodSelectionProbability) {
      neighbourType = "NEIGHBOR" ;
    } else {
      neighbourType = "POPULATION" ;
    }

    List<DoubleSolution> matingPool = matingSelection(currentSolutionIndex, solutionList, 2, neighbourType) ;
    matingPool.add(solutionList.get(currentSolutionIndex)) ;

    return matingPool ;
  }

  public int getCurrentSolutionIndex() {
    return currentSolutionIndex ;
  }

  public String getNeighbourType() {
    return neighbourType ;
  }

  /**
   *
   * @param subproblemId the id of current subproblem
   * @param neighborType neighbour type
   */
  private List<DoubleSolution> matingSelection(
      int subproblemId,
      List<DoubleSolution> population,
      int numberOfSolutionsToSelect,
      String neighborType) {
    int neighbourSize;
    DoubleSolution selectedSolution;

    List<DoubleSolution> listOfSolutions = new ArrayList<>(numberOfSolutionsToSelect) ;
    List<DoubleSolution> neighbors = neighborhood.getNeighbors(population, subproblemId) ;

    neighbourSize = neighbors.size() ;
    while (listOfSolutions.size() < numberOfSolutionsToSelect) {
      int randomPosition;
      if (neighborType.equals("NEIGHBOR")) {
        randomPosition = JMetalRandom.getInstance().nextInt(0, neighbourSize - 1);
        selectedSolution = neighbors.get(randomPosition) ;
      } else {
        randomPosition = JMetalRandom.getInstance().nextInt(0, population.size() - 1) ;
        selectedSolution = population.get(randomPosition);
      }

      boolean flag = true;
      for (DoubleSolution individualId : listOfSolutions) {
        if (individualId == selectedSolution) {
          flag = false;
          break;
        }
      }
    }

    return listOfSolutions ;
  }
}
