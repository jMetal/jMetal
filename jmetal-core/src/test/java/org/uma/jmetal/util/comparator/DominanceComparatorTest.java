//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.util.comparator;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @version 1.0
 */
public class DominanceComparatorTest {
  private DominanceComparator comparator ;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test public void shouldCompareRaiseAnExceptionIfTheFirstSolutionIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("Solution1 is null"));

    comparator = new DominanceComparator() ;

    Solution solution2 = mock(Solution.class) ;

    comparator.compare(null, solution2) ;
  }

  @Test public void shouldCompareRaiseAnExceptionIfTheSecondSolutionIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("Solution2 is null"));

    comparator = new DominanceComparator(0) ;

    Solution solution2 = mock(Solution.class) ;

    comparator.compare(solution2, null) ;
  }

  @Test public void shouldCompareRaiseAnExceptionIfTheSolutionsHaveNotTheSameNumberOfObjectives() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("Cannot compare because solution1 has 4 objectives "
        + "and solution2 has 2"));

    comparator = new DominanceComparator() ;

    Solution solution1 = mock(Solution.class) ;
    Solution solution2 = mock(Solution.class) ;

    when(solution1.getNumberOfObjectives()).thenReturn(4) ;
    when(solution2.getNumberOfObjectives()).thenReturn(2) ;

    comparator.compare(solution1, solution2) ;
  }

  @Test public void shouldCompareReturnTheValueReturnedByTheConstraintViolationComparator() {
    ConstraintViolationComparator violationComparator = mock(ConstraintViolationComparator.class) ;

    Solution solution1 = mock(Solution.class) ;
    Solution solution2 = mock(Solution.class) ;

    when(violationComparator.compare(solution1, solution2)).thenReturn(-1) ;
    comparator = new DominanceComparator(violationComparator) ;
    int obtainedValue = comparator.compare(solution1, solution2) ;

    assertEquals(-1, obtainedValue) ;
    verify(violationComparator).compare(solution1, solution2) ;
  }

  @Test public void shouldCompareReturnZeroIfTheTwoSolutionsHasOneObjectiveWithTheSameValue() {
    ConstraintViolationComparator violationComparator = mock(ConstraintViolationComparator.class) ;

    Solution solution1 = mock(Solution.class) ;
    Solution solution2 = mock(Solution.class) ;

    when(violationComparator.compare(solution1, solution2)).thenReturn(0) ;

    when(solution1.getNumberOfObjectives()).thenReturn(1) ;
    when(solution2.getNumberOfObjectives()).thenReturn(1) ;

    when(solution1.getObjective(0)).thenReturn(4.0) ;
    when(solution2.getObjective(0)).thenReturn(4.0) ;

    comparator = new DominanceComparator(violationComparator) ;

    assertEquals(0, comparator.compare(solution1, solution2));

    verify(violationComparator).compare(solution1, solution2) ;
    verify(solution1, times(3)).getNumberOfObjectives();
    verify(solution1).getObjective(0);
    verify(solution2).getObjective(0);
  }

  @Test public void shouldCompareReturnOneIfTheTwoSolutionsHasOneObjectiveAndTheSecondOneIsLower() {
    ConstraintViolationComparator violationComparator = mock(ConstraintViolationComparator.class) ;

    Solution solution1 = mock(Solution.class) ;
    Solution solution2 = mock(Solution.class) ;

    when(violationComparator.compare(solution1, solution2)).thenReturn(0) ;

    when(solution1.getNumberOfObjectives()).thenReturn(1) ;
    when(solution2.getNumberOfObjectives()).thenReturn(1) ;

    when(solution1.getObjective(0)).thenReturn(4.0) ;
    when(solution2.getObjective(0)).thenReturn(2.0) ;

    comparator = new DominanceComparator(violationComparator) ;

    assertEquals(1, comparator.compare(solution1, solution2));

    verify(violationComparator).compare(solution1, solution2) ;
    verify(solution1, times(3)).getNumberOfObjectives();
    verify(solution1).getObjective(0);
    verify(solution2).getObjective(0);
  }

  @Test public void shouldCompareReturnMinusOneIfTheTwoSolutionsHasOneObjectiveAndTheFirstOneIsLower() {
    ConstraintViolationComparator violationComparator = mock(ConstraintViolationComparator.class) ;

    Solution solution1 = mock(Solution.class) ;
    Solution solution2 = mock(Solution.class) ;

    when(violationComparator.compare(solution1, solution2)).thenReturn(0) ;

    when(solution1.getNumberOfObjectives()).thenReturn(1) ;
    when(solution2.getNumberOfObjectives()).thenReturn(1) ;

    when(solution1.getObjective(0)).thenReturn(-1.0) ;
    when(solution2.getObjective(0)).thenReturn(2.0) ;

    comparator = new DominanceComparator(violationComparator) ;

    assertEquals(-1, comparator.compare(solution1, solution2));

    verify(violationComparator).compare(solution1, solution2) ;
    verify(solution1, times(3)).getNumberOfObjectives();
    verify(solution1).getObjective(0);
    verify(solution2).getObjective(0);
  }

  /**
   * Case A: solution1 has objectives [-1.0, 5.0, 9.0] and solution2 has [2.0, 6.0, 15.0]
   */
  @Test public void shouldCompareReturnMinusOneIfTheFirstSolutionDominatesTheSecondOneCaseA() {
    ConstraintViolationComparator violationComparator = mock(ConstraintViolationComparator.class) ;

    Solution solution1 = mock(Solution.class) ;
    Solution solution2 = mock(Solution.class) ;

    when(violationComparator.compare(solution1, solution2)).thenReturn(0) ;

    when(solution1.getNumberOfObjectives()).thenReturn(3) ;
    when(solution2.getNumberOfObjectives()).thenReturn(3) ;

    when(solution1.getObjective(0)).thenReturn(-1.0) ;
    when(solution1.getObjective(1)).thenReturn(5.0) ;
    when(solution1.getObjective(2)).thenReturn(9.0) ;
    when(solution2.getObjective(0)).thenReturn(2.0) ;
    when(solution2.getObjective(1)).thenReturn(6.0) ;
    when(solution2.getObjective(2)).thenReturn(15.0) ;

    comparator = new DominanceComparator(violationComparator) ;

    assertEquals(-1, comparator.compare(solution1, solution2));

    verify(violationComparator).compare(solution1, solution2) ;
    verify(solution1, times(5)).getNumberOfObjectives();
    verify(solution1, times(3)).getObjective(anyInt());
    verify(solution2, times(3)).getObjective(anyInt());
  }

  /**
   * Case B: solution1 has objectives [-1.0, 5.0, 9.0] and solution2 has [-1.0, 5.0, 10.0]
   */
  @Test public void shouldCompareReturnMinusOneIfTheFirstSolutionDominatesTheSecondOneCaseB() {
    ConstraintViolationComparator violationComparator = mock(ConstraintViolationComparator.class) ;

    Solution solution1 = mock(Solution.class) ;
    Solution solution2 = mock(Solution.class) ;

    when(violationComparator.compare(solution1, solution2)).thenReturn(0) ;

    when(solution1.getNumberOfObjectives()).thenReturn(3) ;
    when(solution2.getNumberOfObjectives()).thenReturn(3) ;

    when(solution1.getObjective(0)).thenReturn(-1.0) ;
    when(solution1.getObjective(1)).thenReturn(5.0) ;
    when(solution1.getObjective(2)).thenReturn(9.0) ;
    when(solution2.getObjective(0)).thenReturn(-1.0) ;
    when(solution2.getObjective(1)).thenReturn(5.0) ;
    when(solution2.getObjective(2)).thenReturn(10.0) ;

    comparator = new DominanceComparator(violationComparator) ;

    assertEquals(-1, comparator.compare(solution1, solution2));

    verify(violationComparator).compare(solution1, solution2) ;
    verify(solution1, times(5)).getNumberOfObjectives();
    verify(solution1, times(3)).getObjective(anyInt());
    verify(solution2, times(3)).getObjective(anyInt());
  }

  /**
   * Case C: solution1 has  objectives [-1.0, 5.0, 9.0] and solution2 has [-2.0, 5.0, 9.0]
   */
  @Test public void shouldCompareReturnOneIfTheSecondSolutionDominatesTheFirstOneCaseC() {
    ConstraintViolationComparator violationComparator = mock(ConstraintViolationComparator.class) ;

    BinarySolution solution1 = mock(BinarySolution.class) ;
    BinarySolution solution2 = mock(BinarySolution.class) ;

    when(violationComparator.compare(solution1, solution2)).thenReturn(0) ;

    when(solution1.getNumberOfObjectives()).thenReturn(3) ;
    when(solution2.getNumberOfObjectives()).thenReturn(3) ;

    when(solution1.getObjective(0)).thenReturn(-1.0) ;
    when(solution1.getObjective(1)).thenReturn(5.0) ;
    when(solution1.getObjective(2)).thenReturn(9.0) ;
    when(solution2.getObjective(0)).thenReturn(-2.0) ;
    when(solution2.getObjective(1)).thenReturn(5.0) ;
    when(solution2.getObjective(2)).thenReturn(9.0) ;

    comparator = new DominanceComparator(violationComparator) ;

    assertEquals(1, comparator.compare(solution1, solution2));

    verify(violationComparator).compare(solution1, solution2) ;
    verify(solution1, times(5)).getNumberOfObjectives();
    verify(solution1, times(3)).getObjective(anyInt());
    verify(solution2, times(3)).getObjective(anyInt());
  }

  /**
   * Case D: solution1 has  objectives [-1.0, 5.0, 9.0] and solution2 has [-1.0, 5.0, 8.0]
   */
  @Test public void shouldCompareReturnOneIfTheSecondSolutionDominatesTheFirstOneCaseD() {
    ConstraintViolationComparator violationComparator = mock(ConstraintViolationComparator.class) ;

    Solution solution1 = mock(Solution.class) ;
    Solution solution2 = mock(Solution.class) ;

    when(violationComparator.compare(solution1, solution2)).thenReturn(0) ;

    when(solution1.getNumberOfObjectives()).thenReturn(3) ;
    when(solution2.getNumberOfObjectives()).thenReturn(3) ;

    when(solution1.getObjective(0)).thenReturn(-1.0) ;
    when(solution1.getObjective(1)).thenReturn(5.0) ;
    when(solution1.getObjective(2)).thenReturn(9.0) ;
    when(solution2.getObjective(0)).thenReturn(-1.0) ;
    when(solution2.getObjective(1)).thenReturn(5.0) ;
    when(solution2.getObjective(2)).thenReturn(8.0) ;

    comparator = new DominanceComparator(violationComparator) ;

    assertEquals(1, comparator.compare(solution1, solution2));

    verify(violationComparator).compare(solution1, solution2) ;
    verify(solution1, times(5)).getNumberOfObjectives();
    verify(solution1, times(3)).getObjective(anyInt());
    verify(solution2, times(3)).getObjective(anyInt());
  }

  //TODO Tests for epsilon dominance are missing
}
