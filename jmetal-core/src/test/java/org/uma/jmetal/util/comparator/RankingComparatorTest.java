package org.uma.jmetal.util.comparator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.Ranking;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class RankingComparatorTest {
  private RankingComparator<Solution<?>> comparator ;

  @Before public void setup() {
    comparator = new RankingComparator<Solution<?>>() ;
  }

  @Test public void shouldCompareReturnOneIfTheFirstSolutionIsNull() {
    Solution<?> solution2 = mock(Solution.class) ;

    assertEquals(1, comparator.compare(null, solution2)) ;
  }

  @Test public void shouldCompareReturnMinusOneIfTheSecondSolutionIsNull() {
    Solution<?> solution1 = mock(Solution.class) ;

    assertEquals(-1, comparator.compare(solution1, null)) ;
  }

  @Test public void shouldCompareReturnZeroIfBothSolutionsAreNull() {
    assertEquals(0, comparator.compare(null, null)) ;
  }

  @Test public void shouldCompareReturnZeroIfBothSolutionsHaveNoRankingAttribute() {
    @SuppressWarnings("unchecked")
    Ranking<Solution<?>> ranking = mock(Ranking.class) ;
    when(ranking.getAttribute(any(Solution.class))).thenReturn((Integer) null, (Integer) null) ;

    ReflectionTestUtils.setField(comparator, "ranking", ranking);

    Solution<?> solution1 = mock(Solution.class) ;
    Solution<?> solution2 = mock(Solution.class) ;

    assertEquals(0, comparator.compare(solution1, solution2));
    verify(ranking, times(2)).getAttribute(any(Solution.class)) ;
  }

  @Test public void shouldCompareReturnZeroIfBothSolutionsHaveTheSameRanking() {
    @SuppressWarnings("unchecked")
    Ranking<Solution<?>> ranking = mock(Ranking.class) ;
    when(ranking.getAttribute(any(DoubleSolution.class))).thenReturn(1, 1, 1, 1) ;

    ReflectionTestUtils.setField(comparator, "ranking", ranking);

    DoubleSolution solution1 = mock(DoubleSolution.class) ;
    DoubleSolution solution2 = mock(DoubleSolution.class) ;

    assertEquals(0, comparator.compare(solution1, solution2));
    verify(ranking, times(4)).getAttribute(any(Solution.class)) ;
  }

  @Test public void shouldCompareReturnMinusOneIfSolutionAHasLessRanking() {
    @SuppressWarnings("unchecked")
    Ranking<Solution<?>> ranking = mock(Ranking.class) ;
    when(ranking.getAttribute(any(BinarySolution.class))).thenReturn(0, 0, 2, 2) ;

    ReflectionTestUtils.setField(comparator, "ranking", ranking);

    BinarySolution solution1 = mock(BinarySolution.class) ;
    BinarySolution solution2 = mock(BinarySolution.class) ;

    assertEquals(-1, comparator.compare(solution1, solution2));
    verify(ranking, times(4)).getAttribute(any(Solution.class)) ;
  }

  @Test public void shouldCompareReturnOneIfSolutionBHasLessRanking() {
    @SuppressWarnings("unchecked")
    Ranking<Solution<?>> ranking = mock(Ranking.class) ;
    when(ranking.getAttribute(any(BinarySolution.class))).thenReturn(3, 3, 2, 2) ;

    ReflectionTestUtils.setField(comparator, "ranking", ranking);

    BinarySolution solution1 = mock(BinarySolution.class) ;
    BinarySolution solution2 = mock(BinarySolution.class) ;

    assertEquals(1, comparator.compare(solution1, solution2));
    verify(ranking, times(4)).getAttribute(any(Solution.class)) ;
  }

}
