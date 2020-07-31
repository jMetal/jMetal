package org.uma.jmetal.experimental.component.catalogue.ranking;

import org.uma.jmetal.experimental.component.catalogue.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class FastNonDominatedSortingRankingTest extends NonDominanceRankingTestCases<Ranking<DoubleSolution>> {
  public FastNonDominatedSortingRankingTest() {
    setRanking(new FastNonDominatedSortRanking<DoubleSolution>()) ;
  }
}

