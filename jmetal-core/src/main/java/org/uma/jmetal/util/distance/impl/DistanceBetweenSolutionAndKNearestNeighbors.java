package org.uma.jmetal.util.distance.impl;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.distance.Distance;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class for calculating the Euclidean distance between two {@link DoubleSolution} objects in solution space.
 *
 * @author <antonio@lcc.uma.es>
 */
public class DistanceBetweenSolutionAndKNearestNeighbors<S extends Solution<Double>, L extends List<S>>
        implements Distance<S, L> {

  private final int k ;
  private Distance<S, S> distance ;

  public DistanceBetweenSolutionAndKNearestNeighbors(int k, Distance<S, S> distance) {
    this.k = k ;
    this.distance = distance ;
  }

  public DistanceBetweenSolutionAndKNearestNeighbors(int k) {
    this(k, new EuclideanDistanceBetweenSolutionsInSolutionSpace<S>()) ;
  }

  /**
   * Computes the knn distance. If the solution list size is lower than k, then k = size in the computation
   * @param solution
   * @param solutionList
   * @return
   */
  @Override
  public double getDistance(S solution, L solutionList) {
    List<Double> listOfDistances = knnDistances(solution, solutionList) ;
    listOfDistances.sort(Comparator.naturalOrder());

    int limit = Math.min(k, listOfDistances.size()) ;

    double result ;
    if (limit == 0) {
      result = 0.0 ;
    } else {
      double sum = 0.0;
      for (int i = 0; i < limit; i++) {
        sum += listOfDistances.get(i);
      }
      result = sum/limit ;
    }
    return result;
  }

  /**
   * Computes the distance between a solution and the solutions of a list. Distances equal to 0 are ignored.
   * @param solution
   * @param solutionList
   * @return A list with the distances
   */
  private List<Double> knnDistances(S solution, L solutionList) {
    List<Double> listOfDistances = new ArrayList<>() ;
    for (int i = 0 ; i< solutionList.size(); i++) {
      double distanceBetweenSolutions = distance.getDistance(solution, solutionList.get(i)) ;
      if (distanceBetweenSolutions != 0) {
        listOfDistances.add(distanceBetweenSolutions) ;
      }
    }

    return listOfDistances ;
  }
}
