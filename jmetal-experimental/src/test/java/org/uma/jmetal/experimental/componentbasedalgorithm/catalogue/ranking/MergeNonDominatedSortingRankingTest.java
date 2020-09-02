package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ranking;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ranking.impl.MergeNonDominatedSortRanking;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class MergeNonDominatedSortingRankingTest extends NonDominanceRankingTestCases<Ranking<DoubleSolution>> {
  public MergeNonDominatedSortingRankingTest() {
    setRanking(new MergeNonDominatedSortRanking<>());
  }
}
