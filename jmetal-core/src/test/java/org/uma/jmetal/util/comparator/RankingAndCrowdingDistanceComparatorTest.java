package org.uma.jmetal.util.comparator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.solution.Solution;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @version 1.0
 */
public class RankingAndCrowdingDistanceComparatorTest {
  private RankingAndCrowdingDistanceComparator<Solution<?>> comparator ;

  @Before public void setup() {
    comparator = new RankingAndCrowdingDistanceComparator<Solution<?>>() ;
  }

  @After public void teardown() {
    comparator = null ;
  }

  @Test public void shouldCompareTwoNullSolutionsReturnZero() {
    assertEquals(0, comparator.compare(null, null)) ;
  }

  @Test public void shouldCompareWithANullSolutionAsFirstArgumentReturnOne() {
    Solution<?> solution = mock(Solution.class) ;
    assertEquals(1, comparator.compare(null, solution)) ;
  }

  @Test public void shouldCompareWithANullSolutionAsSecondArgumentReturnMinusOne() {
    Solution<?> solution = mock(Solution.class) ;
    assertEquals(-1, comparator.compare(solution, null)) ;
  }

  @Test public void shouldCompareWithNullRankingAttributeSolutionAsFirstArgumentReturnOne() {
    Solution<?> solution2 = mock(Solution.class) ;

    assertEquals(1, comparator.compare(null, solution2)) ;
  }

  @Test public void shouldCompareWithRankingYieldingANonZeroValueReturnThatValue() {
    @SuppressWarnings("unchecked")
    Comparator<Solution<?>> rankComparator = mock(Comparator.class) ;

    when(rankComparator.compare(any(Solution.class), any(Solution.class))).thenReturn(1) ;

    ReflectionTestUtils.setField(comparator, "rankComparator", rankComparator);

    Solution<?> solution1 = mock(Solution.class) ;
    Solution<?> solution2 = mock(Solution.class) ;
    assertEquals(1, comparator.compare(solution1, solution2)) ;
    verify(rankComparator).compare(solution1, solution2) ;
  }

  @Test public void shouldCompareWhenRankingYieldingAZeroReturnTheCrowdingDistanceValue() {
    @SuppressWarnings("unchecked")
    Comparator<Solution<?>> rankComparator = mock(Comparator.class) ;
    when(rankComparator.compare(any(Solution.class), any(Solution.class))).thenReturn(0) ;

    @SuppressWarnings("unchecked")
    Comparator<Solution<?>> crowdingDistanceComparator = mock(CrowdingDistanceComparator.class) ;
    when(crowdingDistanceComparator.compare(any(Solution.class), any(Solution.class))).thenReturn(-1) ;

    ReflectionTestUtils.setField(comparator, "rankComparator", rankComparator);
    ReflectionTestUtils.setField(comparator, "crowdingDistanceComparator", crowdingDistanceComparator);

    Solution<?> solution1 = mock(Solution.class) ;
    Solution<?> solution2 = mock(Solution.class) ;

    assertEquals(-1, comparator.compare(solution1, solution2)) ;
    verify(rankComparator).compare(solution1, solution2) ;
    verify(crowdingDistanceComparator).compare(solution1, solution2) ;
  }
}
