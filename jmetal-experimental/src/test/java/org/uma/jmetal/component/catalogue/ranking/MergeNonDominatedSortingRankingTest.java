package org.uma.jmetal.component.catalogue.ranking;

import org.uma.jmetal.component.catalogue.ranking.impl.MergeNonDominatedSortRanking;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class MergeNonDominatedSortingRankingTest extends NonDominanceRankingTestCases<Ranking<DoubleSolution>> {
  public MergeNonDominatedSortingRankingTest() {
    setRanking(new MergeNonDominatedSortRanking<>());
  }
}
