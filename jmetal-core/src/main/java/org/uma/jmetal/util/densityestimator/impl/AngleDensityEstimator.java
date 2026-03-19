package org.uma.jmetal.util.densityestimator.impl;

import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class implements an angle-based density estimator for multi-objective optimization.
 *
 * <p>The estimator computes the angular diversity of each solution by measuring the angles
 * between solutions in the objective space relative to a reference point (typically the ideal
 * point or origin). Solutions with larger minimum angles to their neighbors are considered
 * more diverse and receive higher density values.
 *
 * <p>This approach is particularly effective for many-objective optimization problems where
 * traditional density estimators like crowding distance become less effective.
 *
 * <p>References:
 * <ul>
 *   <li>Liu, Y., et al.: An angle dominance criterion for evolutionary many-objective
 *       optimization. Information Sciences 509 (2020): 376-399</li>
 *   <li>Xiang, Y., et al.: A vector angle-based evolutionary algorithm for unconstrained
 *       many-objective optimization. IEEE TEVC 21.1 (2016): 131-152</li>
 * </ul>
 *
 * @author Antonio J. Nebro
 * @param <S> The solution type
 */
public class AngleDensityEstimator<S extends Solution<?>> implements DensityEstimator<S> {
  private final String attributeId = getClass().getName();
  private final double[] referencePoint;
  private final boolean normalize;
  private final int numberOfNeighbors;

  private static final double EPSILON = 1e-10;

  /**
   * Full constructor.
   *
   * @param referencePoint The reference point for angle calculations (typically ideal point).
   *                       If null, the origin (0,0,...,0) will be used.
   * @param normalize Whether to normalize objectives before computing angles
   * @param numberOfNeighbors Number of nearest angular neighbors to consider (typically 2)
   */
  public AngleDensityEstimator(double[] referencePoint, boolean normalize, int numberOfNeighbors) {
    Check.that(numberOfNeighbors >= 1, "Number of neighbors must be at least 1");
    this.referencePoint = referencePoint != null ? referencePoint.clone() : null;
    this.normalize = normalize;
    this.numberOfNeighbors = numberOfNeighbors;
  }

  /**
   * Constructor with reference point and normalization option.
   *
   * @param referencePoint The reference point for angle calculations
   * @param normalize Whether to normalize objectives
   */
  public AngleDensityEstimator(double[] referencePoint, boolean normalize) {
    this(referencePoint, normalize, 2);
  }

  /**
   * Constructor with reference point only.
   *
   * @param referencePoint The reference point for angle calculations
   */
  public AngleDensityEstimator(double[] referencePoint) {
    this(referencePoint, false, 2);
  }

  /**
   * Constructor with normalization option only.
   *
   * @param normalize Whether to normalize objectives
   */
  public AngleDensityEstimator(boolean normalize) {
    this(null, normalize, 2);
  }

  /**
   * Default constructor. Uses origin as reference point, no normalization, 2 neighbors.
   */
  public AngleDensityEstimator() {
    this(null, false, 2);
  }

  /**
   * Computes the angle-based density for all solutions in the list.
   *
   * @param solutionList The list of solutions to evaluate
   */
  @Override
  public void compute(List<S> solutionList) {
    int size = solutionList.size();

    if (size == 0) {
      return;
    }

    if (size == 1) {
      solutionList.get(0).attributes().put(attributeId, Double.POSITIVE_INFINITY);
      return;
    }

    int numberOfObjectives = solutionList.get(0).objectives().length;

    // Get objective values matrix
    double[][] solutionMatrix;
    if (normalize) {
      solutionMatrix = NormalizeUtils.normalize(SolutionListUtils.getMatrixWithObjectiveValues(solutionList));
    } else {
      solutionMatrix = SolutionListUtils.getMatrixWithObjectiveValues(solutionList);
    }

    // Determine effective reference point
    double[] effectiveReferencePoint = referencePoint != null 
        ? referencePoint 
        : new double[numberOfObjectives]; // Default to origin

    // Translate solutions relative to reference point
    double[][] translatedSolutions = new double[size][numberOfObjectives];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < numberOfObjectives; j++) {
        translatedSolutions[i][j] = solutionMatrix[i][j] - effectiveReferencePoint[j];
      }
    }

    // Compute angle matrix between all pairs of solutions
    double[][] angleMatrix = new double[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = i + 1; j < size; j++) {
        double angle = computeAngle(translatedSolutions[i], translatedSolutions[j]);
        angleMatrix[i][j] = angle;
        angleMatrix[j][i] = angle;
      }
    }

    // Calculate density based on minimum angles to k nearest angular neighbors
    int k = Math.min(numberOfNeighbors, size - 1);
    for (int i = 0; i < size; i++) {
      double[] angles = new double[size - 1];
      int idx = 0;
      for (int j = 0; j < size; j++) {
        if (i != j) {
          angles[idx++] = angleMatrix[i][j];
        }
      }
      
      // Sort angles to find k smallest (nearest angular neighbors)
      java.util.Arrays.sort(angles);
      
      // Density is the sum of the k smallest angles
      // Larger minimum angles = more diversity = higher density (should be kept)
      double density = 0.0;
      for (int n = 0; n < k; n++) {
        density += angles[n];
      }
      
      solutionList.get(i).attributes().put(attributeId, density);
    }

    // Protect extreme solutions by setting their density to maximum
    for (int i = 0; i < numberOfObjectives; i++) {
      solutionList.sort(new ObjectiveComparator<>(i));
      solutionList.get(0).attributes().put(attributeId, Double.POSITIVE_INFINITY);
      solutionList.get(solutionList.size() - 1).attributes().put(attributeId, Double.POSITIVE_INFINITY);
    }
  }

  /**
   * Computes the angle between two vectors in radians.
   *
   * @param vector1 First vector
   * @param vector2 Second vector
   * @return The angle in radians [0, π]
   */
  private double computeAngle(double[] vector1, double[] vector2) {
    double dotProduct = 0.0;
    double norm1 = 0.0;
    double norm2 = 0.0;

    for (int i = 0; i < vector1.length; i++) {
      dotProduct += vector1[i] * vector2[i];
      norm1 += vector1[i] * vector1[i];
      norm2 += vector2[i] * vector2[i];
    }

    norm1 = Math.sqrt(norm1);
    norm2 = Math.sqrt(norm2);

    if (norm1 < EPSILON || norm2 < EPSILON) {
      return 0.0; // Degenerate case: one or both vectors are at the reference point
    }

    double cosAngle = dotProduct / (norm1 * norm2);
    
    // Clamp to [-1, 1] to handle numerical errors
    cosAngle = Math.max(-1.0, Math.min(1.0, cosAngle));

    return Math.acos(cosAngle);
  }

  @Override
  public Double value(S solution) {
    Check.notNull(solution);
    Object value = solution.attributes().get(attributeId);
    Check.notNull(value);
    return (Double) value;
  }

  @Override
  public Comparator<S> comparator() {
    return Comparator.comparing(this::value);
  }

  public String attributeId() {
    return attributeId;
  }

  /**
   * Returns a comparator that sorts solutions by density in descending order.
   * Solutions with higher density values (more diverse) come first.
   *
   * @return A reversed comparator for density-based sorting
   */
  public Comparator<S> reversedComparator() {
    return comparator().reversed();
  }

  /**
   * Gets the reference point used for angle calculations.
   *
   * @return The reference point, or null if using origin
   */
  public double[] getReferencePoint() {
    return referencePoint != null ? referencePoint.clone() : null;
  }

  /**
   * Returns whether normalization is enabled.
   *
   * @return true if normalization is enabled
   */
  public boolean isNormalize() {
    return normalize;
  }

  /**
   * Gets the number of neighbors used for density calculation.
   *
   * @return The number of neighbors
   */
  public int getNumberOfNeighbors() {
    return numberOfNeighbors;
  }
}
