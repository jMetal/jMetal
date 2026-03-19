package org.uma.jmetal.util.densityestimator.impl;

import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.KNearestDistanceCalculator;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements a density estimator based on the distance to the k-th nearest solution
 *
 * @author Antonio J. Nebro
 */
public class KnnDensityEstimator<S extends Solution<?>> implements DensityEstimator<S> {

  private final String attributeId = getClass().getName();
  private final KNearestDistanceCalculator kNearestDistanceCalculator;
  private final int k;

  public KnnDensityEstimator(int k) {
    this(k, false);
  }

  public KnnDensityEstimator(int k, boolean normalize) {
    this.k = k;
    this.kNearestDistanceCalculator = new KNearestDistanceCalculator(k, normalize) ;
  }

  /**
   * Assigns the KNearestDistanceCalculator distance to all the solutions in a list
   *
   * @param solutionList
   */
  @Override
  public void compute(List<S> solutionList) {
    double[][] distanceMatrix;
    int size = solutionList.size();

    Check.that(size > 0, "The solution list size must be greater than zero");
    if (size <= k) {
      return;
    }

    /* Compute the distance matrix */
    distanceMatrix = SolutionListUtils.getMatrixWithObjectiveValues(solutionList) ;

    double[] distances = kNearestDistanceCalculator.compute(distanceMatrix);

    /* Get the k-nearest distance of all the solutions */
    for (int i = 0; i < solutionList.size(); i++) {
      solutionList.get(i).attributes().put(attributeId, distances[i]);
    }
  }

  @Override
  public Double value(S solution) {
    Check.notNull(solution);

    Double result = 0.0;
    if (solution.attributes().get(attributeId) != null) {
      result = (Double) solution.attributes().get(attributeId);
    }
    return result;
  }

  @Override
  public Comparator<S> comparator() {
    return Comparator.comparing(this::value);
  }
}
