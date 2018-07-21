package org.uma.jmetal.util.neighborhood.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.distance.Distance;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenSolutionsInObjectiveSpace;
import org.uma.jmetal.util.neighborhood.Neighborhood;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements a neighborhood that select the k-nearest solutions according to a
 * distance measure. By default, the Euclidean distance between objectives is used.
 *
 * @param <S>
 */
public class KNearestNeighborhood<S extends Solution<?>> implements Neighborhood<S> {
  private int neighborSize;
  private Distance<S, S> distance;

  public KNearestNeighborhood(int neighborSize) {
    this(neighborSize, new EuclideanDistanceBetweenSolutionsInObjectiveSpace<S>());
  }

  public KNearestNeighborhood(int neighborSize, Distance<S, S> distance) {
    this.neighborSize = neighborSize;
    this.distance = distance;
  }

  @Override
  public List<S> getNeighbors(List<S> solutionList, int solutionIndex) {
    List<S> neighbourSolutions = new ArrayList<>();
    double[] distances = new double[solutionList.size()];
    int[] indexes = new int[solutionList.size()];

    for (int i = 0; i < solutionList.size(); i++) {
      distances[i] = this.distance.getDistance(solutionList.get(i), solutionList.get(solutionIndex));
      indexes[i] = i;
    }

    minFastSort(distances, indexes, solutionList.size(), neighborSize);


    for (int i = 1; i <= neighborSize; i++) {
      neighbourSolutions.add(solutionList.get(indexes[i]));
    }

    return neighbourSolutions;
  }

  private void minFastSort(double x[], int idx[], int n, int m) {
    for (int i = 0; i < m; i++) {
      for (int j = i + 1; j < n; j++) {
        if (x[i] > x[j]) {
          double temp = x[i];
          x[i] = x[j];
          x[j] = temp;
          int id = idx[i];
          idx[i] = idx[j];
          idx[j] = id;
        }
      }
    }
  }
}
