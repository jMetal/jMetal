package org.uma.jmetal.component.ranking;

import org.uma.jmetal.component.ranking.impl.ExperimentalFastNonDominanceRanking;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class ExperimentalFastNonDominatedSortingRankingTest extends NonDominanceRankingTestCases<Ranking<DoubleSolution>> {
  public ExperimentalFastNonDominatedSortingRankingTest() {
    this.ranking = new ExperimentalFastNonDominanceRanking<DoubleSolution>() ;
  }
}

