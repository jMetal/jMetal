package org.uma.jmetal.util.distance.impl;

import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.distance.Distance;
import weka.core.ListOptions;

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

  private int k ;
  private Distance<S, S> distance ;

  public DistanceBetweenSolutionAndKNearestNeighbors(int k, Distance<S, S> distance) {
    this.k = k ;
    this.distance = distance ;
  }

  public DistanceBetweenSolutionAndKNearestNeighbors(int k) {
    this(k, new EuclideanDistanceBetweenSolutionsInSolutionSpace<S>()) ;
  }

  @Override
  public double getDistance(S solution, L solutionList) {
    List<Double> listOfDistances = knnDistances(solution, solutionList) ;
    listOfDistances.sort(Comparator.naturalOrder());

    double sum = 0.0 ;
    for (int i = 0 ; i < k; i++) {
      sum += listOfDistances.get(i) ;
    }

    return Math.sqrt(sum/k);
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
