package org.uma.jmetal.util.archive.impl;

import java.util.Comparator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.dominanceComparator.DominanceComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.AngleDensityEstimator;

/**
 * Archive based on angle-based density estimator. Solutions with smaller minimum angles
 * to their nearest neighbors (less angular diversity) are pruned first to maintain diversity.
 *
 * <p>This archive is particularly effective for many-objective optimization problems where
 * traditional crowding distance becomes less effective due to the curse of dimensionality.
 *
 * @author Antonio J. Nebro
 * @param <S> The solution type
 */
@SuppressWarnings("serial")
public class AngleArchive<S extends Solution<?>> extends AbstractBoundedArchive<S> {
  private final Comparator<S> angleComparator;
  private final DensityEstimator<S> angleDensityEstimator;

  /**
   * Full constructor.
   *
   * @param maxSize Maximum archive size
   * @param referencePoint Reference point for angle calculations (null uses origin)
   * @param normalize Whether to normalize objectives
   * @param numberOfNeighbors Number of nearest angular neighbors to consider
   * @param dominanceComparator Dominance comparator for archive management
   */
  public AngleArchive(int maxSize, double[] referencePoint, boolean normalize, 
      int numberOfNeighbors, DominanceComparator<S> dominanceComparator) {
    super(maxSize, dominanceComparator);
    angleDensityEstimator = new AngleDensityEstimator<>(referencePoint, normalize, numberOfNeighbors);
    // Higher density value = more diverse = better. Reversed comparator ensures findWorstSolution
    // returns the one with lowest density value (least diverse) to be pruned.
    angleComparator = Comparator.comparing(angleDensityEstimator::value).reversed();
  }

  /**
   * Constructor with reference point, normalization, and number of neighbors.
   *
   * @param maxSize Maximum archive size
   * @param referencePoint Reference point for angle calculations
   * @param normalize Whether to normalize objectives
   * @param numberOfNeighbors Number of nearest angular neighbors to consider
   */
  public AngleArchive(int maxSize, double[] referencePoint, boolean normalize, int numberOfNeighbors) {
    this(maxSize, referencePoint, normalize, numberOfNeighbors, new DefaultDominanceComparator<>());
  }

  /**
   * Constructor with reference point and normalization option.
   *
   * @param maxSize Maximum archive size
   * @param referencePoint Reference point for angle calculations
   * @param normalize Whether to normalize objectives
   */
  public AngleArchive(int maxSize, double[] referencePoint, boolean normalize) {
    this(maxSize, referencePoint, normalize, 2);
  }

  /**
   * Constructor with reference point only.
   *
   * @param maxSize Maximum archive size
   * @param referencePoint Reference point for angle calculations
   */
  public AngleArchive(int maxSize, double[] referencePoint) {
    this(maxSize, referencePoint, false, 2);
  }

  /**
   * Constructor with normalization option only.
   *
   * @param maxSize Maximum archive size
   * @param normalize Whether to normalize objectives
   */
  public AngleArchive(int maxSize, boolean normalize) {
    this(maxSize, null, normalize, 2);
  }

  /**
   * Simple constructor with archive size only.
   *
   * @param maxSize Maximum archive size
   */
  public AngleArchive(int maxSize) {
    this(maxSize, null, false, 2);
  }

  @Override
  public void prune() {
    if (solutions().size() > maximumSize()) {
      computeDensityEstimator();
      S worst = new SolutionListUtils().findWorstSolution(solutions(), angleComparator);
      solutions().remove(worst);
    }
  }

  @Override
  public Comparator<S> comparator() {
    return angleComparator;
  }

  @Override
  public void computeDensityEstimator() {
    angleDensityEstimator.compute(solutions());
  }
}
