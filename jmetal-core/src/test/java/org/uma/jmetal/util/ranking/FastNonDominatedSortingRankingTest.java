package org.uma.jmetal.util.ranking;

import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class FastNonDominatedSortingRankingTest extends NonDominanceRankingTestCases<Ranking<DoubleSolution>> {
  public FastNonDominatedSortingRankingTest() {
    setRanking(new FastNonDominatedSortRanking<DoubleSolution>()) ;
  }
}

