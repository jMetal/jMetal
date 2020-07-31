package org.uma.jmetal.experimental.component.catalogue.ranking;

import org.uma.jmetal.experimental.component.catalogue.ranking.impl.ExperimentalFastNonDominanceRanking;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class ExperimentalFastNonDominatedSortingRankingTest extends NonDominanceRankingTestCases<Ranking<DoubleSolution>> {
  public ExperimentalFastNonDominatedSortingRankingTest() {
    setRanking(new ExperimentalFastNonDominanceRanking<DoubleSolution>()) ;
  }
}

