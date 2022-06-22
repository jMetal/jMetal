package org.uma.jmetal.auto.component.util;

import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.ranking.Ranking;

/**
 * A preference is a list composed of a ranking and a density estimator that specifies preferences
 * in the selection and replacement components of an evolutionary algorithm.
 *
 * @author Antonio J. Nebro
 */
public class RankingAndDensityEstimatorPreference<S> {
  private Ranking<S> ranking;
  private DensityEstimator<S> densityEstimator;
  private RankingAndDensityEstimatorPreference<S> relatedPreference;
  private boolean preferenceHasBeenComputedFirstTime = false;

  public RankingAndDensityEstimatorPreference(Ranking<S> ranking, DensityEstimator<S> densityEstimator) {
    this(ranking, densityEstimator, null);
  }

  /** Constructor */
  public RankingAndDensityEstimatorPreference(
          Ranking<S> ranking, DensityEstimator<S> densityEstimator, RankingAndDensityEstimatorPreference<S> relatedPreference) {
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
    return densityEstimator.getClass() != relatedPreference.getDensityEstimator().getClass() ;
  }

  private void recomputeDensityEstimator() {
    for (int i = 0; i < ranking.getNumberOfSubFronts(); i++) {
      densityEstimator.compute(ranking.getSubFront(i));
    }
  }

  private void recomputeRanking(List<S> solutionList) {
    ranking.compute(solutionList);
  }

  private boolean rankingsAreDifferent() {
    return !ranking.getClass().equals(relatedPreference.getRanking().getClass());
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
            Comparator.comparing(getRanking()::getRank), Comparator.comparing(getDensityEstimator()::getValue).reversed()));
  }
}
