package org.uma.jmetal.auto.util.preference;

import org.uma.jmetal.auto.util.attribute.Attribute;
import org.uma.jmetal.auto.util.densityestimator.DensityEstimator;
import org.uma.jmetal.auto.util.ranking.Ranking;
import org.uma.jmetal.qualityindicator.QualityIndicator;

import java.util.List;

/**
 *
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class Preference<S>  {
  private Ranking<S> ranking ;
  private DensityEstimator<S> densityEstimator ;

  public Preference(Ranking<S> ranking, DensityEstimator<S> densityEstimator) {
    this.ranking = ranking ;
    this.densityEstimator = densityEstimator ;
  }

  public Ranking<S> getRanking() {
    return ranking ;
  }

  public DensityEstimator<S> getDensityEstimator() {
    return densityEstimator ;
  }
}
