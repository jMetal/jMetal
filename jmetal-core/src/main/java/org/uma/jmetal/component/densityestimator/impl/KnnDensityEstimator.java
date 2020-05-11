package org.uma.jmetal.component.densityestimator.impl;

import org.uma.jmetal.component.densityestimator.DensityEstimator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.AttributeComparator;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.impl.DoubleValueAttributeComparator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.checking.Check;
import org.uma.jmetal.util.distance.Distance;
import org.uma.jmetal.util.distance.impl.EuclideanDistanceBetweenVectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class implements the a density estimator based on the distance to the k-th nearest solution
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class KnnDensityEstimator<S extends Solution<?>> implements DensityEstimator<S> {
  private String attributeId = getClass().getName();
  private Comparator<S> solutionComparator;
  private Distance<double[], double[]> distance = new EuclideanDistanceBetweenVectors();
  private int k;
  private double[][] distanceMatrix;
  private boolean normalize ;

  public KnnDensityEstimator(int k) {
    this(k, false) ;
  }

  public KnnDensityEstimator(int k, boolean normalize) {
    this.k = k;
    solutionComparator =
            new DoubleValueAttributeComparator<>(attributeId, AttributeComparator.Ordering.DESCENDING);
    this.normalize = normalize ;
  }

  /**
   * Assigns the KNN distance to all the solutions in a list
   *
   * @param solutionList
   */
  @Override
  public void computeDensityEstimator(List<S> solutionList) {
    int size = solutionList.size();

    Check.that(size > 0, "The solution list size must be greater than zero");
    if (size <= k) {
      return;
    }

    /* Compute the distance matrix */
    distanceMatrix = new double[solutionList.size()][solutionList.size()];

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

    for (int i = 0; i < solutionList.size(); i++) {
      for (int j = i + 1; j < solutionList.size(); j++) {
        distanceMatrix[i][j] = distance.compute(solutionMatrix[i], solutionMatrix[j]) ;
        distanceMatrix[j][i] = distanceMatrix[i][j] ;
      }
    }

    /* Get the k-nearest distance of all the solutions */
    for (int i = 0; i < solutionList.size(); i++) {
      List<Double> distances = new ArrayList<>();
      for (int j = 0; j < solutionList.size(); j++) {
        distances.add(distanceMatrix[i][j]);
      }
      distances.sort(Comparator.naturalOrder());
      solutionList.get(i).setAttribute(attributeId, distances.get(k));
    }
  }

  @Override
  public String getAttributeId() {
    return attributeId;
  }

  @Override
  public Comparator<S> getSolutionComparator() {
    return solutionComparator;
  }

  @Override
  public List<S> sort(List<S> solutionList) {
    for (int i = 0; i < solutionList.size(); i++) {
      List<Double> distances = new ArrayList<>();
      for (int j = 0; j < solutionList.size(); j++) {
        distances.add(distanceMatrix[i][j]);
      }
      distances.sort(Comparator.naturalOrder());
      solutionList.get(i).setAttribute("DISTANCES_", distances);
    }

    Collections.sort(
        solutionList,
        (s1, s2) -> {
          List<Double> d1 = (List<Double>) s1.getAttribute("DISTANCES_");
          List<Double> d2 = (List<Double>) s2.getAttribute("DISTANCES_");
          if (k >= d1.size()) {
            return 0;
          } else {
            int localK = k;
            if (d1.get(localK) > d2.get(localK)) {
              return -1;
            } else if (d1.get(localK) < d2.get(localK)) {
              return +1;
            } else {
              while (localK < (d1.size() - 1)) {
                localK++;
                if (d1.get(localK) > d2.get(localK)) {
                  return -1;
                } else if (d1.get(localK) < d2.get(localK)) {
                  return +1;
                }
              }
              return 0;
            }
          }
        });

    return solutionList;
  }
}
