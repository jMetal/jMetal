package org.uma.jmetal.component.ranking;

import org.uma.jmetal.component.ranking.impl.ExperimentalFastNonDominanceRanking;
import org.uma.jmetal.component.ranking.impl.FastNonDominatedSortRanking;

public class ExperimentalFastNonDominatedSortingRankingTest extends NonDominanceRankingTestCases{
  public ExperimentalFastNonDominatedSortingRankingTest() {
    this.ranking = new ExperimentalFastNonDominanceRanking() ;
  }
}

