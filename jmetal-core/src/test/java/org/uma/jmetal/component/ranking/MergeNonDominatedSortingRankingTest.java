package org.uma.jmetal.component.ranking;

import org.uma.jmetal.component.ranking.impl.MergeNonDominatedSortRanking;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class MergeNonDominatedSortingRankingTest extends NonDominanceRankingTestCases<Ranking<DoubleSolution>> {
  public MergeNonDominatedSortingRankingTest() {
    this.ranking = new MergeNonDominatedSortRanking<DoubleSolution>();
  }
}
