package org.uma.jmetal.util.ranking;

import org.uma.jmetal.util.ranking.impl.ExperimentalFastNonDominanceRanking;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class ExperimentalFastNonDominatedSortingRankingTest extends NonDominanceRankingTestCases<Ranking<DoubleSolution>> {
  public ExperimentalFastNonDominatedSortingRankingTest() {
    setRanking(new ExperimentalFastNonDominanceRanking<DoubleSolution>()) ;
  }
}

