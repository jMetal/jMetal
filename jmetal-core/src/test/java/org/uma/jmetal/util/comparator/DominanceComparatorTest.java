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
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @version 1.0
 */
public class DominanceComparatorTest {
  private DominanceComparator comparator ;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test public void shouldCompareReturnOneIfTheFirstSolutionIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("Solution1 is null"));

    comparator = new DominanceComparator() ;

    Solution solution2 = mock(Solution.class) ;

    comparator.compare(null, solution2) ;
  }

  @Test public void shouldCompareReturnMinusOneIfTheSecondSolutionIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("Solution2 is null"));

    comparator = new DominanceComparator(0) ;

    Solution solution2 = mock(Solution.class) ;

    comparator.compare(solution2, null) ;
  }

  @Test public void shouldCompareReturnTheValueReturnedByTheConstraintViolationComparator() {
    ConstraintViolationComparator violationComparator = mock(ConstraintViolationComparator.class) ;

    Solution solution1 = mock(Solution.class) ;
    Solution solution2 = mock(Solution.class) ;

    when(violationComparator.compare(solution1, solution2)).thenReturn(-1) ;
    comparator = new DominanceComparator(violationComparator) ;

    assertEquals(-1, comparator.compare(solution1, solution2)) ;
    verify(violationComparator).compare(solution1, solution2) ;
  }
}
