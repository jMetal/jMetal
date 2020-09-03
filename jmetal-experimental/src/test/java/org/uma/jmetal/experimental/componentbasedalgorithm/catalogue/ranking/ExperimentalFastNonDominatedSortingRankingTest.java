package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ranking;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ranking.impl.ExperimentalFastNonDominanceRanking;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class ExperimentalFastNonDominatedSortingRankingTest extends NonDominanceRankingTestCases<Ranking<DoubleSolution>> {
  public ExperimentalFastNonDominatedSortingRankingTest() {
    setRanking(new ExperimentalFastNonDominanceRanking<DoubleSolution>()) ;
  }
}

