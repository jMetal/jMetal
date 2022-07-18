package org.uma.jmetal.util.densityestimator.impl;

import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.distance.Distance;
import org.uma.jmetal.util.distance.impl.CosineSimilarityBetweenVectors;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.point.Point;

/**
 * This class implements the a density estimator based on the cosine similarity
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class CosineSimilarityDensityEstimator<S extends Solution<?>> implements DensityEstimator<S> {
  private final String attributeId = getClass().getName();
  private Distance<double[], double[]> distance;
  private Point referencePoint;
  private boolean normalize;

  public CosineSimilarityDensityEstimator(Point referencePoint) {
    this(referencePoint, true);
  }

  public CosineSimilarityDensityEstimator(@NotNull Point referencePoint, boolean normalize) {
    this.referencePoint = referencePoint;
    distance = new CosineSimilarityBetweenVectors(referencePoint.getValues());
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

    if (size == 0) {
      return;
    }

    if (size == 1) {
      solutionList.get(0).attributes().put(attributeId, 0.0);
    }

    var numberOfObjectives = solutionList.get(0).objectives().length;

    if (size == numberOfObjectives) {
      for (var solution : solutionList) {
        solution.attributes().put(attributeId, 0.0);
      }

      return;
    }

    for (@NotNull S solution : solutionList) {
      referencePoint.update(solution.objectives());
    }

    var distanceMatrix = new double[solutionList.size()][solutionList.size()];
    double[][] solutionMatrix = null;
    if (normalize) {
      try {
        solutionMatrix = NormalizeUtils.normalize(SolutionListUtils.getMatrixWithObjectiveValues(solutionList));
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

    for (var i = 0; i < solutionList.size(); i++) {
      var currentMaximumDistance = 0.0;
      var secondCurrentMaximumDistance = 0.0;

      for (var j = 0; j < solutionList.size(); j++) {
        if (i != j) {
          var d = distanceMatrix[i][j];

          if (d >= currentMaximumDistance) {
            secondCurrentMaximumDistance = currentMaximumDistance;
            currentMaximumDistance = d;
          } else if (d > secondCurrentMaximumDistance) {
            secondCurrentMaximumDistance = d;
          }
        }
      }

      solutionList
          .get(i)
          .attributes().put(attributeId, (currentMaximumDistance + secondCurrentMaximumDistance));
      solutionList
          .get(i)
          .attributes().put("DIFF", Math.abs(currentMaximumDistance - secondCurrentMaximumDistance));
    }

    for (var i = 0; i < solutionList.get(0).objectives().length; i++) {
      solutionList.sort(new ObjectiveComparator<S>(i));
      solutionList.get(solutionList.size() - 1).attributes().put(attributeId, 0.0);
    }
  }

  @Override
  public Double getValue(S solution) {
    Check.notNull(solution);

    Double result = 0.0 ;
    if (solution.attributes().get(attributeId) != null) {
      result = (Double) solution.attributes().get(attributeId) ;
    }
    return result ;
  }

  @Override
  public Comparator<S> getComparator() {
    return Comparator.comparing(this::getValue) ;
  }
}
