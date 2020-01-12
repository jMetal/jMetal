package org.uma.jmetal.component.ranking;

import org.uma.jmetal.component.ranking.impl.MergeNonDominatedSortRanking;

public class MergeNonDominatedSortingRankingTest extends NonDominanceRankingTestCases {
  public MergeNonDominatedSortingRankingTest() {
    this.ranking = new MergeNonDominatedSortRanking();
  }
}
