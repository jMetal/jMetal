package org.uma.jmetal.util.distance.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.distance.Distance;

/**
 * Class for calculating the Euclidean distance between two {@link DoubleSolution} objects in solution space.
 *
 * @author <antonio@lcc.uma.es>
 */
public class DistanceBetweenSolutionAndKNearestNeighbors<S extends Solution<?>>
        implements Distance<S, List<S>> {

  private final int k ;
  private Distance<S, S> distance ;

  public DistanceBetweenSolutionAndKNearestNeighbors(int k, Distance<S, S> distance) {
    this.k = k ;
    this.distance = distance ;
  }

  /**
   * Computes the knn distance. If the solution list size is lower than k, then k = size in the computation
   * @param solution
   * @param solutionList
   * @return
   */
  @Override
  public double compute(S solution, List<S> solutionList) {
    List<Double> listOfDistances = knnDistances(solution, solutionList) ;
    listOfDistances.sort(Comparator.naturalOrder());

    int limit = Math.min(k, listOfDistances.size()) ;

    double result ;
    if (limit == 0) {
      result = 0.0 ;
    } else {
      result = listOfDistances.get(limit-1) ;
    }
    return result;
  }

  /**
   * Computes the distance between a solution and the solutions of a list. Distances equal to 0 are ignored.
   * @param solution
   * @param solutionList
   * @return A list with the distances
   */
  private List<Double> knnDistances(S solution, List<S> solutionList) {
    List<Double> listOfDistances = new ArrayList<>();
    for (S s : solutionList) {
      double distanceBetweenSolutions = distance.compute(solution, s);
      if (distanceBetweenSolutions != 0) {
        Double aDouble = distanceBetweenSolutions;
        listOfDistances.add(aDouble);
      }
    }

    return listOfDistances ;
  }
}
