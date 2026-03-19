package org.uma.jmetal.util.densityestimator.impl;

import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.distance.Distance;
import org.uma.jmetal.util.distance.impl.CosineSimilarityBetweenVectors;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements a density estimator based on the cosine similarity
 *
 * @author Antonio J. Nebro
 */
public class CosineSimilarityDensityEstimator<S extends Solution<?>> implements DensityEstimator<S> {
  private final String attributeId = getClass().getName();
  private final double[] referencePoint;
  private final boolean normalize;

  public CosineSimilarityDensityEstimator(double[] referencePoint, boolean normalize) {
    this.referencePoint = referencePoint != null ? referencePoint.clone() : null;
    this.normalize = normalize;
  }

  public CosineSimilarityDensityEstimator(double[] referencePoint) {
    this(referencePoint, false);
  }

  public CosineSimilarityDensityEstimator(boolean normalize) {
    this(null, normalize);
  }

  public CosineSimilarityDensityEstimator() {
    this(null, false);
  }

  /**
   * Assigns the distance to all the solutions in a list
   *
   * @param solutionList
   */
  @Override
  public void compute(List<S> solutionList) {
    int size = solutionList.size();

    if (size == 0) {
      return;
    }

    if (size == 1) {
      solutionList.get(0).attributes().put(attributeId, 0.0);
    }

    int numberOfObjectives = solutionList.get(0).objectives().length;

    if (size == numberOfObjectives) {
      for (S solution : solutionList) {
        solution.attributes().put(attributeId, 0.0);
      }

      return;
    }

    double[][] solutionMatrix;
    if (normalize) {
      solutionMatrix = NormalizeUtils.normalize(SolutionListUtils.getMatrixWithObjectiveValues(solutionList));
    } else {
      solutionMatrix = SolutionListUtils.getMatrixWithObjectiveValues(solutionList);
    }

    double[] effectiveReferencePoint = referencePoint != null ? referencePoint : new double[numberOfObjectives];
    Distance<double[], double[]> distance = new CosineSimilarityBetweenVectors(effectiveReferencePoint);
    double[][] distanceMatrix = new double[solutionList.size()][solutionList.size()];

    for (int i = 0; i < solutionList.size(); i++) {
      for (int j = i + 1; j < solutionList.size(); j++) {
        distanceMatrix[i][j] = distance.compute(solutionMatrix[i], solutionMatrix[j]);
        distanceMatrix[j][i] = distanceMatrix[i][j];
      }
    }

    // Calculate angular diversity: for each solution, find the sum of angular distances
    // to its two nearest angular neighbors
    for (int i = 0; i < solutionList.size(); i++) {
      double highestSimilarity = Double.NEGATIVE_INFINITY;
      double secondHighestSimilarity = Double.NEGATIVE_INFINITY;

      for (int j = 0; j < solutionList.size(); j++) {
        if (i != j) {
          double similarity = distanceMatrix[i][j];

          if (similarity > highestSimilarity) {
            secondHighestSimilarity = highestSimilarity;
            highestSimilarity = similarity;
          } else if (similarity > secondHighestSimilarity) {
            secondHighestSimilarity = similarity;
          }
        }
      }

      // Convert similarity to angular distance: 1 - similarity
      // Higher angular distance = more diverse = higher density value (should be kept)
      double angularDistance = (2.0 - highestSimilarity - secondHighestSimilarity);
      solutionList.get(i).attributes().put(attributeId, angularDistance);
    }

    // Protect extreme solutions by setting their density to maximum (most diverse)
    for (int i = 0; i < solutionList.get(0).objectives().length; i++) {
      solutionList.sort(new ObjectiveComparator<>(i));
      solutionList.get(0).attributes().put(attributeId, Double.POSITIVE_INFINITY);
      solutionList.get(solutionList.size() - 1).attributes().put(attributeId, Double.POSITIVE_INFINITY);
    }
  }

  @Override
  public Double value(S solution) {
    Check.notNull(solution);

    Double result = 0.0 ;
    if (solution.attributes().get(attributeId) != null) {
      result = (Double) solution.attributes().get(attributeId) ;
    }
    return result ;
  }

  @Override
  public Comparator<S> comparator() {
    return Comparator.comparing(this::value) ;
  }
}
