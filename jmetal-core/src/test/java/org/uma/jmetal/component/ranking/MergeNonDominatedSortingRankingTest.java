package org.uma.jmetal.component.ranking;

import org.uma.jmetal.component.ranking.impl.MergeSortNonDominatedSortRanking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MergeNonDominatedSortingRankingTest extends NonDominanceRankingTestCases {
  public MergeNonDominatedSortingRankingTest() {
    this.ranking = new MergeSortNonDominatedSortRanking();
  }
}
