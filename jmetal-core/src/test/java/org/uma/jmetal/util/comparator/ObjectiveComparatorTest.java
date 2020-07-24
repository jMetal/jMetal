package org.uma.jmetal.util.comparator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @version 1.0
 */
public class ObjectiveComparatorTest {
  private ObjectiveComparator<Solution<?>> comparator ;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test public void shouldCompareReturnOneIfTheFirstSolutionIsNull() {
    comparator = new ObjectiveComparator<Solution<?>>(0) ;

    Solution<?> solution2 = mock(Solution.class) ;

    assertEquals(1, comparator.compare(null, solution2)) ;
  }

  @Test public void shouldCompareReturnMinusOneIfTheSecondSolutionIsNull() {
    comparator = new ObjectiveComparator<Solution<?>>(0) ;

    Solution<?> solution1 = mock(Solution.class) ;

    assertEquals(-1, comparator.compare(solution1, null)) ;
  }

  @Test public void shouldCompareReturnZeroIfBothSolutionsAreNull() {
    comparator = new ObjectiveComparator<Solution<?>>(0) ;

    assertEquals(0, comparator.compare(null, null)) ;
  }

  @Test public void shouldCompareReturnMinusOneIfTheObjectiveOfSolution1IsLower() {
    comparator = new ObjectiveComparator<Solution<?>>(0) ;

    DoubleSolution solution1 = mock(DoubleSolution.class) ;
    DoubleSolution solution2 = mock(DoubleSolution.class) ;

    when(solution1.objectives().size()).thenReturn(4) ;
    when(solution2.objectives().size()).thenReturn(4) ;

    when(solution1.objectives().get(0)).thenReturn(-4.0) ;
    when(solution2.objectives().get(0)).thenReturn(5.0) ;

    assertEquals(-1, comparator.compare(solution1, solution2)) ;
    verify(solution1).objectives().get(0) ;
    verify(solution2).objectives().get(0) ;
  }

  @Test public void shouldCompareReturnOneIfTheObjectiveOfSolution2IsLower() {
    comparator = new ObjectiveComparator<Solution<?>>(2) ;

    DoubleSolution solution1 = mock(DoubleSolution.class) ;
    DoubleSolution solution2 = mock(DoubleSolution.class) ;

    when(solution1.objectives().size()).thenReturn(4) ;
    when(solution2.objectives().size()).thenReturn(4) ;

    when(solution1.objectives().get(2)).thenReturn(7.0) ;
    when(solution2.objectives().get(2)).thenReturn(5.0) ;

    assertEquals(1, comparator.compare(solution1, solution2)) ;
  }

  @Test public void shouldCompareReturnZeroIfTheObjectiveOfTheSolutionsIsTheSame() {
    comparator = new ObjectiveComparator<Solution<?>>(2) ;

    DoubleSolution solution1 = mock(DoubleSolution.class) ;
    DoubleSolution solution2 = mock(DoubleSolution.class) ;

    when(solution1.objectives().size()).thenReturn(4) ;
    when(solution2.objectives().size()).thenReturn(4) ;

    when(solution1.objectives().get(2)).thenReturn(7.0) ;
    when(solution2.objectives().get(2)).thenReturn(7.0) ;

    assertEquals(0, comparator.compare(solution1, solution2)) ;
  }

  @Test public void shouldCompareReturnMinusOneIfTheObjectiveOfSolution1IsGreaterInDescendingOrder() {
    comparator = new ObjectiveComparator<Solution<?>>(0, ObjectiveComparator.Ordering.DESCENDING) ;

    DoubleSolution solution1 = mock(DoubleSolution.class) ;
    DoubleSolution solution2 = mock(DoubleSolution.class) ;

    when(solution1.objectives().size()).thenReturn(4) ;
    when(solution2.objectives().size()).thenReturn(4) ;

    when(solution1.objectives().get(0)).thenReturn(25.0) ;
    when(solution2.objectives().get(0)).thenReturn(5.0) ;

    assertEquals(-1, comparator.compare(solution1, solution2)) ;
    verify(solution1).objectives().get(0) ;
    verify(solution2).objectives().get(0) ;
  }

  @Test public void shouldCompareReturnOneIfTheObjectiveOfSolution2IsGreaterInDescendingOrder() {
    comparator = new ObjectiveComparator<Solution<?>>(2, ObjectiveComparator.Ordering.DESCENDING) ;

    DoubleSolution solution1 = mock(DoubleSolution.class) ;
    DoubleSolution solution2 = mock(DoubleSolution.class) ;

    when(solution1.objectives().size()).thenReturn(4) ;
    when(solution2.objectives().size()).thenReturn(4) ;

    when(solution1.objectives().get(2)).thenReturn(7.0) ;
    when(solution2.objectives().get(2)).thenReturn(25.0) ;

    assertEquals(1, comparator.compare(solution1, solution2)) ;

    verify(solution1).objectives().get(2) ;
    verify(solution2).objectives().get(2) ;
    verify(solution1).objectives().size();
    verify(solution2).objectives().size();
  }

  @Test public void shouldCompareRaiseAnExceptionIfSolution1HasLessObjectivesThanTheOneRequested() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution1 has 3 objectives and the objective "
        + "to sort is 5"));

    comparator = new ObjectiveComparator<Solution<?>>(5, ObjectiveComparator.Ordering.DESCENDING) ;

    DoubleSolution solution1 = mock(DoubleSolution.class) ;
    DoubleSolution solution2 = mock(DoubleSolution.class) ;

    when(solution1.objectives().size()).thenReturn(3) ;
    when(solution2.objectives().size()).thenReturn(6) ;

    comparator.compare(solution1, solution2) ;

    verify(solution1).objectives().size();
    verify(solution2).objectives().size();
  }

  @Test public void shouldCompareRaiseAnExceptionIfSolution2HasLessObjectivesThanTheOneRequested() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution2 has 5 objectives and the objective "
        + "to sort is 5"));

    comparator = new ObjectiveComparator<Solution<?>>(5, ObjectiveComparator.Ordering.DESCENDING) ;

    DoubleSolution solution1 = mock(DoubleSolution.class) ;
    DoubleSolution solution2 = mock(DoubleSolution.class) ;

    when(solution1.objectives().size()).thenReturn(7) ;
    when(solution2.objectives().size()).thenReturn(5) ;

    comparator.compare(solution1, solution2) ;

    verify(solution1).objectives().size();
    verify(solution2).objectives().size();
  }
}
