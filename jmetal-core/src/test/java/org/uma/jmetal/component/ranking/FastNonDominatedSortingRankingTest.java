package org.uma.jmetal.component.ranking;

import org.uma.jmetal.component.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class FastNonDominatedSortingRankingTest extends NonDominanceRankingTestCases<Ranking<DoubleSolution>> {
  public FastNonDominatedSortingRankingTest() {
    this.ranking = new FastNonDominatedSortRanking<DoubleSolution>() ;
  }
}

