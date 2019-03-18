package org.uma.jmetal.auto.component.replacement.impl;

import org.uma.jmetal.auto.component.replacement.Replacement;
import org.uma.jmetal.auto.component.selection.impl.RankingAndDensityEstimatorMatingPoolSelection;
import org.uma.jmetal.auto.util.densityestimator.DensityEstimator;
import org.uma.jmetal.auto.util.ranking.Ranking;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;

public class RankingAndDensityEstimatorReplacement<S extends Solution<?>> implements Replacement<S> {
  public Ranking<S> ranking ;
  public DensityEstimator<S> densityEstimator ;

  public RankingAndDensityEstimatorReplacement(Ranking<S> ranking, DensityEstimator<S> densityEstimator) {
    this.ranking = ranking ;
    this.densityEstimator = densityEstimator ;
  }

  public List<S> replace(List<S> solutionList, List<S> offspringList) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(solutionList);
    jointPopulation.addAll(offspringList);

    RankingAndDensityEstimatorMatingPoolSelection<S> selection ;
    selection = new RankingAndDensityEstimatorMatingPoolSelection<S>(solutionList.size(), ranking, densityEstimator);

    return selection.select(jointPopulation) ;
  }
}
