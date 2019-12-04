package org.uma.jmetal.component.ranking;

import org.uma.jmetal.component.ranking.impl.FastNonDominatedSortRanking;

public class FastNonDominatedSortingRankingTest extends NonDominanceRankingTestCases{
  public FastNonDominatedSortingRankingTest() {
    this.ranking = new FastNonDominatedSortRanking() ;
  }
}

