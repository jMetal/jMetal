package org.uma.jmetal.util.ranking;

import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ranking.impl.MergeNonDominatedSortRanking;

public class MergeNonDominatedSortingRankingTest extends NonDominanceRankingTestCases<Ranking<DoubleSolution>> {
  public MergeNonDominatedSortingRankingTest() {
    setRanking(new MergeNonDominatedSortRanking<>());
  }
}
