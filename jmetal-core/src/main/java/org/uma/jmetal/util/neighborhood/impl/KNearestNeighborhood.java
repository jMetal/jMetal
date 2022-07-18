package org.uma.jmetal.util.neighborhood.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.distance.Distance;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenSolutionsInObjectiveSpace;
import org.uma.jmetal.util.neighborhood.Neighborhood;

/**
 * This class implements a neighborhood that select the k-nearest solutions according to a
 * distance measure. By default, the Euclidean distance between objectives is used.
 *
 * @param <S>
 */
@SuppressWarnings("serial")
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
    var distances = new double[solutionList.size()];
    var indexes = new int[solutionList.size()];

    for (var i = 0; i < solutionList.size(); i++) {
      distances[i] = this.distance.compute(solutionList.get(i), solutionList.get(solutionIndex));
      indexes[i] = i;
    }

    minFastSort(distances, indexes, solutionList.size(), neighborSize);


      List<S> list = new ArrayList<>();
    var bound = neighborSize;
      for (var i = 1; i <= bound; i++) {
        var s = solutionList.get(indexes[i]);
          list.add(s);
      }
    var neighbourSolutions = list;

    return neighbourSolutions;
  }

  private void minFastSort(double x[], int idx[], int n, int m) {
    for (var i = 0; i < m; i++) {
      for (var j = i + 1; j < n; j++) {
        if (x[i] > x[j]) {
          var temp = x[i];
          x[i] = x[j];
          x[j] = temp;
          var id = idx[i];
          idx[i] = idx[j];
          idx[j] = id;
        }
      }
    }
  }
}
