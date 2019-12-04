package org.uma.jmetal.component.ranking;

import org.junit.Test;
import org.uma.jmetal.component.ranking.impl.ExperimentalFastNonDominanceRanking;
import org.uma.jmetal.component.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.component.ranking.impl.MergeSortNonDominatedSortRanking;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FastNonDominatedSortingRankingTest extends NonDominanceRankingTestCases{
  public FastNonDominatedSortingRankingTest() {
    this.ranking = new FastNonDominatedSortRanking() ;
  }
}

