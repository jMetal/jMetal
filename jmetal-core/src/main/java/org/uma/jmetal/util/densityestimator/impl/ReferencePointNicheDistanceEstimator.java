package org.uma.jmetal.util.densityestimator.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Density estimator based on perpendicular distance to the closest reference
 * point.
 * Used in NSGA-III for mating selection preference.
 *
 * <p>
 * Solutions closer to reference points (lower distance) are preferred.
 * This estimator also stores the associated reference point index for each
 * solution.
 *
 * @author Antonio J. Nebro
 * @param <S> Type of solution
 */
public class ReferencePointNicheDistanceEstimator<S extends Solution<?>>
    implements DensityEstimator<S> {

  private static final String DISTANCE_ATTRIBUTE_ID = "NICHE_DISTANCE";
  private static final String REFERENCE_POINT_ATTRIBUTE_ID = "REFERENCE_POINT_INDEX";

  private final List<double[]> referencePoints;
  private final int numberOfObjectives;

  // For normalization
  private double[] idealPoint;
  private double[] intercepts;

  /**
   * Constructor with reference points.
   *
   * @param referencePoints    List of reference points (each is a double array)
   * @param numberOfObjectives Number of objectives
   */
  public ReferencePointNicheDistanceEstimator(List<double[]> referencePoints, int numberOfObjectives) {
    Check.notNull(referencePoints);
    Check.that(!referencePoints.isEmpty(), "Reference points list cannot be empty");
    this.referencePoints = referencePoints;
    this.numberOfObjectives = numberOfObjectives;
  }

  @Override
  public void compute(List<S> solutionList) {
    if (solutionList.isEmpty()) {
      return;
    }

    // Step 1: Compute ideal point (minimum in each objective)
    computeIdealPoint(solutionList);

    // Step 2: Translate objectives
    List<double[]> translatedObjectives = translateObjectives(solutionList);

    // Step 3: Compute intercepts (simplified - use maximum translated values)
    computeIntercepts(translatedObjectives);

    // Step 4: Normalize and compute distances
    for (int i = 0; i < solutionList.size(); i++) {
      S solution = solutionList.get(i);
      double[] normalizedObjectives = normalizeObjectives(translatedObjectives.get(i));

      // Find closest reference point
      int closestRefPointIndex = -1;
      double minDistance = Double.MAX_VALUE;

      for (int r = 0; r < referencePoints.size(); r++) {
        double distance = perpendicularDistance(referencePoints.get(r), normalizedObjectives);
        if (distance < minDistance) {
          minDistance = distance;
          closestRefPointIndex = r;
        }
      }

      // Store attributes
      solution.attributes().put(DISTANCE_ATTRIBUTE_ID, minDistance);
      solution.attributes().put(REFERENCE_POINT_ATTRIBUTE_ID, closestRefPointIndex);
    }
  }

  private void computeIdealPoint(List<S> solutionList) {
    idealPoint = new double[numberOfObjectives];
    for (int f = 0; f < numberOfObjectives; f++) {
      idealPoint[f] = Double.MAX_VALUE;
      for (S solution : solutionList) {
        idealPoint[f] = Math.min(idealPoint[f], solution.objectives()[f]);
      }
    }
  }

  private List<double[]> translateObjectives(List<S> solutionList) {
    List<double[]> translated = new ArrayList<>(solutionList.size());
    for (S solution : solutionList) {
      double[] translatedObj = new double[numberOfObjectives];
      for (int f = 0; f < numberOfObjectives; f++) {
        translatedObj[f] = solution.objectives()[f] - idealPoint[f];
      }
      translated.add(translatedObj);
    }
    return translated;
  }

  private void computeIntercepts(List<double[]> translatedObjectives) {
    // Simplified intercept computation: use maximum translated value per objective
    // (Full NSGA-III uses hyperplane construction via extreme points)
    intercepts = new double[numberOfObjectives];
    for (int f = 0; f < numberOfObjectives; f++) {
      intercepts[f] = 0.0;
      for (double[] obj : translatedObjectives) {
        intercepts[f] = Math.max(intercepts[f], obj[f]);
      }
      // Avoid division by zero
      if (intercepts[f] < 1e-10) {
        intercepts[f] = 1e-10;
      }
    }
  }

  private double[] normalizeObjectives(double[] translatedObjectives) {
    double[] normalized = new double[numberOfObjectives];
    for (int f = 0; f < numberOfObjectives; f++) {
      normalized[f] = translatedObjectives[f] / intercepts[f];
    }
    return normalized;
  }

  /**
   * Computes perpendicular distance from a point to a reference direction.
   */
  private double perpendicularDistance(double[] referenceDirection, double[] point) {
    double numerator = 0;
    double denominator = 0;

    for (int i = 0; i < referenceDirection.length; i++) {
      numerator += referenceDirection[i] * point[i];
      denominator += referenceDirection[i] * referenceDirection[i];
    }

    if (denominator < 1e-10) {
      return Double.MAX_VALUE;
    }

    double k = numerator / denominator;

    double distance = 0;
    for (int i = 0; i < referenceDirection.length; i++) {
      distance += Math.pow(k * referenceDirection[i] - point[i], 2.0);
    }

    return Math.sqrt(distance);
  }

  @Override
  public Double value(S solution) {
    Check.notNull(solution);
    Double result = (Double) solution.attributes().get(DISTANCE_ATTRIBUTE_ID);
    return result != null ? result : Double.MAX_VALUE;
  }

  /**
   * Returns the index of the reference point associated with this solution.
   */
  public Integer referencePointIndex(S solution) {
    return (Integer) solution.attributes().get(REFERENCE_POINT_ATTRIBUTE_ID);
  }

  @Override
  public Comparator<S> comparator() {
    // Lower distance = better (preferred for selection)
    return Comparator.comparing(this::value);
  }

  public List<double[]> getReferencePoints() {
    return referencePoints;
  }
}
