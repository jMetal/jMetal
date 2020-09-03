package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ranking;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class FastNonDominatedSortingRankingTest extends NonDominanceRankingTestCases<Ranking<DoubleSolution>> {
  public FastNonDominatedSortingRankingTest() {
    setRanking(new FastNonDominatedSortRanking<DoubleSolution>()) ;
  }
}

