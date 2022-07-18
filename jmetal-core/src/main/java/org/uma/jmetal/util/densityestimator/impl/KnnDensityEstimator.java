package org.uma.jmetal.util.densityestimator.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.distance.Distance;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenVectors;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * This class implements a density estimator based on the distance to the k-th nearest solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class KnnDensityEstimator<S extends Solution<?>> implements DensityEstimator<S> {

  private final String attributeId = getClass().getName();
  private Distance<double[], double[]> distance = new EuclideanDistanceBetweenVectors();
  private int k;
  private double[][] distanceMatrix;
  private boolean normalize;

  public KnnDensityEstimator(int k) {
    this(k, false);
  }

  public KnnDensityEstimator(int k, boolean normalize) {
    this.k = k;
    this.normalize = normalize;
  }

  /**
   * Assigns the KNN distance to all the solutions in a list
   *
   * @param solutionList
   */
  @Override
  public void compute(List<S> solutionList) {
    var size = solutionList.size();

    Check.that(size > 0, "The solution list size must be greater than zero");
    if (size <= k) {
      return;
    }

    /* Compute the distance matrix */
    distanceMatrix = new double[solutionList.size()][solutionList.size()];

    double[][] solutionMatrix = null;
    if (normalize) {
      try {
        var m = SolutionListUtils.getMatrixWithObjectiveValues(solutionList);
        solutionMatrix = NormalizeUtils.normalize(m);
      } catch (JMetalException e) {
        e.printStackTrace();
      }
    } else {
      solutionMatrix = SolutionListUtils.getMatrixWithObjectiveValues(solutionList);
    }

    for (var i = 0; i < solutionList.size(); i++) {
      for (var j = i + 1; j < solutionList.size(); j++) {
        distanceMatrix[i][j] = distance.compute(solutionMatrix[i], solutionMatrix[j]);
        distanceMatrix[j][i] = distanceMatrix[i][j];
      }
    }

    /* Get the k-nearest distance of all the solutions */
    for (var i = 0; i < solutionList.size(); i++) {
      @NotNull List<Double> distances = new ArrayList<>();
      for (var j = 0; j < solutionList.size(); j++) {
        distances.add(distanceMatrix[i][j]);
      }
      distances.sort(Comparator.naturalOrder());
      solutionList.get(i).attributes().put(attributeId, distances.get(k));
    }
  }

  private boolean checkMatrixRowsAreEqual(double[][] matrix) {
    var numberOfColumns = matrix[0].length;

      for (var i = 1; i < numberOfColumns; i++) {
          if (!checkColumnValuesAreEqual(matrix, i)) {
              return false;
          }
      }
      return true;
  }

  private boolean checkColumnValuesAreEqual(double[] @NotNull [] matrix, int column) {
    var columnValue = matrix[0][column];

      for (var i = 1; i < matrix.length; i++) {
          if (matrix[i][column] != columnValue) {
              return false;
          }
      }
      return true;
  }

  @Override
  public Double getValue(S solution) {
    Check.notNull(solution);

    Double result = 0.0;
    if (solution.attributes().get(attributeId) != null) {
      result = (Double) solution.attributes().get(attributeId);
    }
    return result;
  }

  @Override
  public Comparator<S> getComparator() {
    return Comparator.comparing(this::getValue);
  }
}
