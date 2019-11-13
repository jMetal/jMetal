package org.uma.jmetal.util.comparator;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @version 1.0
 */
public class CrowdingDistanceComparatorTest {
  private CrowdingDistanceComparator<Solution<?>> comparator ;

  @Before public void setup() {
    comparator = new CrowdingDistanceComparator<Solution<?>>() ;
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

  @Test public void shouldCompareReturnZeroIfBothSolutionsHaveNoCrowdingDistanceAttribute() {
    @SuppressWarnings("unchecked")
    CrowdingDistance<Solution<?>> distance = mock(CrowdingDistance.class) ;
    when(distance.getAttribute(any(Solution.class))).thenReturn((Double) null, (Double) null) ;

    ReflectionTestUtils.setField(comparator, "crowdingDistance", distance);

    Solution<?> solution1 = mock(Solution.class) ;
    Solution<?> solution2 = mock(Solution.class) ;

    assertEquals(0, comparator.compare(solution1, solution2));
    verify(distance, times(2)).getAttribute(any(Solution.class)) ;
  }

  @Test public void shouldCompareReturnZeroIfBothSolutionsHaveTheSameDistance() {
    @SuppressWarnings("unchecked")
    CrowdingDistance<Solution<?>> distance = mock(CrowdingDistance.class) ;
    when(distance.getAttribute(any(DoubleSolution.class))).thenReturn(2.0, 2.0, 2.0, 2.0) ;

    ReflectionTestUtils.setField(comparator, "crowdingDistance", distance);

    DoubleSolution solution1 = mock(DoubleSolution.class) ;
    DoubleSolution solution2 = mock(DoubleSolution.class) ;

    assertEquals(0, comparator.compare(solution1, solution2));
    verify(distance, times(4)).getAttribute(any(Solution.class)) ;
  }

  @Test public void shouldCompareReturnOneIfSolutionAHasLessDistance() {
    @SuppressWarnings("unchecked")
    CrowdingDistance<Solution<?>> distance = mock(CrowdingDistance.class) ;
    when(distance.getAttribute(any(BinarySolution.class))).thenReturn(0.0, 0.0, 2.0, 2.0) ;

    ReflectionTestUtils.setField(comparator, "crowdingDistance", distance);

    BinarySolution solution1 = mock(BinarySolution.class) ;
    BinarySolution solution2 = mock(BinarySolution.class) ;

    assertEquals(1, comparator.compare(solution1, solution2));
    verify(distance, times(4)).getAttribute(any(Solution.class)) ;
  }

  @Test public void shouldCompareReturnMinusOneIfSolutionBHasHigherDistance() {
    @SuppressWarnings("unchecked")
    CrowdingDistance<Solution<?>> distance = mock(CrowdingDistance.class) ;
    when(distance.getAttribute(any(BinarySolution.class))).thenReturn(3.0, 3.0, 2.0, 2.0) ;

    ReflectionTestUtils.setField(comparator, "crowdingDistance", distance);

    BinarySolution solution1 = mock(BinarySolution.class) ;
    BinarySolution solution2 = mock(BinarySolution.class) ;

    assertEquals(-1, comparator.compare(solution1, solution2));
    verify(distance, times(4)).getAttribute(any(Solution.class)) ;
  }
}
