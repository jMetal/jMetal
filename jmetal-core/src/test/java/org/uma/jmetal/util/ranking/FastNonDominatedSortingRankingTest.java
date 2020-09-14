package org.uma.jmetal.util.ranking;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;

public class FastNonDominatedSortingRankingTest extends NonDominanceRankingTestCases<Ranking<DoubleSolution>> {
  public FastNonDominatedSortingRankingTest() {
    setRanking(new FastNonDominatedSortRanking<DoubleSolution>()) ;
  }
}

