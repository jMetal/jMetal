package org.uma.jmetal.util.archive.impl;

import java.util.Comparator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.dominanceComparator.DominanceComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CosineSimilarityDensityEstimator;

/**
 * Archive based on cosine similarity density estimator. Solutions with lower angular diversity
 * (smaller angular distance to nearest neighbors) are pruned first to maintain diversity.
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class CosineSimilarityArchive<S extends Solution<?>> extends AbstractBoundedArchive<S> {
  private final Comparator<S> cosineSimilarityComparator;
  private final DensityEstimator<S> cosineSimilarityDensityEstimator;

  public CosineSimilarityArchive(int maxSize, double[] referencePoint, boolean normalize,
      DominanceComparator<S> dominanceComparator) {
    super(maxSize, dominanceComparator);
    cosineSimilarityDensityEstimator = new CosineSimilarityDensityEstimator<>(referencePoint, normalize);
    // Higher density value = more diverse = better. Reversed comparator ensures findWorstSolution
    // returns the one with lowest density value (least diverse) to be pruned.
    cosineSimilarityComparator = Comparator.comparing(cosineSimilarityDensityEstimator::value).reversed();
  }

  public CosineSimilarityArchive(int maxSize, double[] referencePoint, boolean normalize) {
    this(maxSize, referencePoint, normalize, new DefaultDominanceComparator<>());
  }

  public CosineSimilarityArchive(int maxSize, double[] referencePoint) {
    this(maxSize, referencePoint, false);
  }

  public CosineSimilarityArchive(int maxSize, boolean normalize) {
    this(maxSize, null, normalize);
  }

  public CosineSimilarityArchive(int maxSize) {
    this(maxSize, null, false);
  }

  @Override
  public void prune() {
    if (solutions().size() > maximumSize()) {
      computeDensityEstimator();
      S worst = new SolutionListUtils().findWorstSolution(solutions(), cosineSimilarityComparator);
      solutions().remove(worst);
    }
  }

  @Override
  public Comparator<S> comparator() {
    return cosineSimilarityComparator;
  }

  @Override
  public void computeDensityEstimator() {
    cosineSimilarityDensityEstimator.compute(solutions());
  }
}
