package org.uma.jmetal.util;

import org.uma.jmetal.component.densityestimator.DensityEstimator;
import org.uma.jmetal.component.ranking.Ranking;
import org.uma.jmetal.util.comparator.MultiComparator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * A preference is a list composed of a ranking and a density estimator that specifies preferences
 * in the selection and replacement components of an evolutionary algorithm.
 *
 * @author Antonio J. Nebro
 */
public class Preference<S> {
  private Ranking<S> ranking;
  private DensityEstimator<S> densityEstimator;
  private Preference<S> relatedPreference;
  private boolean preferenceHasBeenComputedFirstTime = false;

  public Preference(Ranking<S> ranking, DensityEstimator<S> densityEstimator) {
    this(ranking, densityEstimator, null);
  }

  /** Constructor */
  public Preference(
      Ranking<S> ranking, DensityEstimator<S> densityEstimator, Preference<S> relatedPreference) {
    this.ranking = ranking;
    this.densityEstimator = densityEstimator;
    this.relatedPreference = relatedPreference;
  }

  /**
   * Recomputes the ranking and density estimators on a solution list if they are different of the
   * ones of the related preference.
   */
  public void recompute(List<S> solutionList) {
    if (!preferenceHasBeenComputedFirstTime) {
      recomputeRanking(solutionList);
      recomputeDensityEstimator();
      preferenceHasBeenComputedFirstTime = true;
    } else if (relatedPreference != null) {
      if (rankingsAreDifferent()) {
        recomputeRanking(solutionList);
        recomputeDensityEstimator();
      } else if (densityEstimatorsAreDifferent()) {
        recomputeDensityEstimator();
      }
    }
  }

  private boolean densityEstimatorsAreDifferent() {
    return !densityEstimator
        .getAttributeId()
        .equals(relatedPreference.getDensityEstimator().getAttributeId());
  }

  private void recomputeDensityEstimator() {
    for (int i = 0; i < ranking.getNumberOfSubFronts(); i++) {
      densityEstimator.computeDensityEstimator(ranking.getSubFront(i));
    }
  }

  private void recomputeRanking(List<S> solutionList) {
    ranking.computeRanking(solutionList);
  }

  private boolean rankingsAreDifferent() {
    return !ranking.getAttributeId().equals(relatedPreference.getRanking().getAttributeId());
  }

  public Ranking<S> getRanking() {
    return ranking;
  }

  public DensityEstimator<S> getDensityEstimator() {
    return densityEstimator;
  }

  public Comparator<S> getComparator() {
    return new MultiComparator<>(
        List.of(
            getRanking().getSolutionComparator(), getDensityEstimator().getSolutionComparator()));
  }
}
